package de.windelknecht.stup.utils.coding.mvc

import java.lang.reflect.{Constructor, Modifier, ParameterizedType}
import java.util.UUID

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import de.windelknecht.stup.utils.coding.EventEntpreller
import de.windelknecht.stup.utils.coding.EventEntpreller.{EventDescr, OnTimerExpired}
import de.windelknecht.stup.utils.coding.mvc.ModelHandler.{ReadFailure, ReadSuccess, smhReadRes}
import de.windelknecht.stup.utils.coding.reactive.Notify
import de.windelknecht.stup.utils.coding.reactive.Notify.{++, NotifyEvent}
import org.reflections.Reflections

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.language.dynamics
import scala.reflect.ClassTag
import scala.reflect.runtime.{currentMirror => cm, universe => ru}
import scala.util.{Failure, Success}

object Model {
  trait smEvent extends NotifyEvent
  /**
   * Some property value has changed.
   */
  case object OnChanged extends smEvent
  case object OnDeleted extends smEvent

  trait smRes
  case class Changed(propertyName: String, oldValue: Any, newValue: Any)
  case class Deleted(err: Option[String])

  /**
   * Private usage.
   */
  private val _reflCtorCache = new mutable.HashMap[String, Constructor[_ <: Model[_ <: Entity]]]()

  /**
   * Create a stup model for this entity.
   */
  def createModel(
    entity: Entity,
    dao: Dao,
    smh: ActorRef
    ): Option[SM] = {
    _reflCtorCache.synchronized {
      _reflCtorCache.get(entity.getClass.getName) match {
        case Some(x) => Some(x.newInstance(dao, entity.id, smh))
        case None => None
      }
    }
  }

  /**
   * Function to add a namespace to look for stup models.
   *
   * TODO: make it async
   */
  def searchHereForModels(namespace: String) { modelCtorLookup(namespace) }

  type SM = Model[_ <: Entity]

  /**
   * Return ctor with our stup model signature.
   */
  @throws[NoSuchMethodException]
  private def getCtor(clazz: Class[_ <: SM]): Constructor[_ <: SM] = clazz.getConstructor(classOf[Dao], classOf[UUID], classOf[ActorRef])

  /**
   * Searches all subclasses of Model in the given path.
   * Used to instantiate (reflection) when a new entity is created.
   */
  private def modelCtorLookup(
    namespace: String
    ) {
    new Reflections(namespace)
      .getSubTypesOf(classOf[SM])
      .filterNot(c=> Modifier.isAbstract(c.getModifiers))
      .foreach{clazz=>
      // get Entity type
      val entityTypeClass = clazz
        .getGenericSuperclass
        .asInstanceOf[ParameterizedType]
        .getActualTypeArguments
        .head.getTypeName

      // trying to find our ctor with the 3 stup model arguments
      val ctor = try {
        Some(getCtor(clazz))
      } catch {
        case e: NoSuchMethodException => // println(e)
          None
      }

      // cache zeuch
      ctor match {
        case Some(x) =>
          _reflCtorCache.synchronized {
            _reflCtorCache += (entityTypeClass -> x)
          }

        case None => // TODO: report error(s"found a Model class (${clazz.getName}}), but the ctor does not match our needs. Must be (Dao, UUID, ActorRef)")
      }
    }
  }
}

abstract class Model[T <: Entity](
  dao: Dao,
  entityId: UUID,
  modelHandler: ActorRef,
  timeoutForDAOAccess: Duration = 10.seconds
  )(implicit classTag: ClassTag[T])
  extends Dynamic
  with Notify {
  import de.windelknecht.stup.utils.coding.mvc.Model._
  implicit val timeout = Timeout(10 seconds)

  // types
  case object eeEntprellEvent

  // fields
  private var _entity: Future[T] = load()
  private val _cachedGetter = doGetterLookup()
  private val _cachedSetter = doSetterLookup()
  private var _isDeleted = false
  private val _eventEntpreller = new EventEntpreller(
    Map(eeEntprellEvent -> (1 second)),
    {
      case (OnTimerExpired, EventDescr(eeEntprellEvent, data)) ++ _ =>
    }
  )

  /**
   * Return the entity id.
   */
  def id = entity.id

  /**
   * Return registered listener cound.
   */
  private[mvc] def listenerCount = registeredListenerCount

  /**
   * This stup model gets marked from staup model handler, that someone has the entity deleted.
   */
  private[mvc] def youAreDeleted() {
    synchronized {
      _isDeleted = true
      _eventEntpreller.shutdown()
    }
    fireOnDeleted()
  }

  /**
   * Method for dynamic getter.
   */
  def selectDynamic[V](name: String): V = {
    getter.get(name) match {
      case Some(x) => x().asInstanceOf[V]
      case None => throw new IllegalArgumentException(s"given entity has no getter named '$name'")
    }
  }

  /**
   * Method for dynamic setter.
   */
  def updateDynamic(name: String)(value: Any) {
    if(name == "id")
      throw new IllegalArgumentException(s"trying to set property 'id', but this is forbidden")
    if(!getter.contains(name))
      throw new IllegalArgumentException(s"a setter with this name '$name' is unknown")

    val (im, mApply, argNames) = setter
    val args = argNames.map{p=>
      if(p == name)
        value
      else if(p == "id")
        id
      else
        selectDynamic(p)
    }

    update(
      im
        .reflectMethod(mApply)(args: _*)
        .asInstanceOf[T],
      propertyName = name,
      oldValue = selectDynamic(name),
      newValue = value
    )
  }

  /**
   * Return entity. For internal use (eg. reading properties).
   */
  protected def entity: T = Await.result(_entity, timeoutForDAOAccess)

  /**
   * This function fires a notify event, when a property value has changed.
   * @param propertyName is the name of the changed property
   * @param oldValue is the old value
   * @param newValue is the new value
   */
  protected def fireOnChanged(
    propertyName: String,
    oldValue: Any,
    newValue: Any
    ) = fireNotify(OnChanged, Changed(propertyName = propertyName, oldValue = oldValue, newValue = newValue))

  /**
   * This function fires a notify event, when the entity is deleted.
   */
  protected def fireOnDeleted(
    err: Option[String] = None
    ) = fireNotify(OnChanged, Deleted(err = err))

  /**
   * Take the entity and searches all getters and put it into a list for later use.
   */
  private def doGetterLookup(): Future[Map[String, ru.MethodMirror]] = {
    Future {
      val im = cm.reflect(cm.reflect(entity).instance)
      val ts = im.symbol.typeSignature

      ts
        .members
        .filter(s => s.isMethod && s.asMethod.isGetter && s.name.toString != "id")
        .map(_.asMethod)
        .map{m=> m.name.toString -> im.reflectMethod(m.asMethod)}
        .toMap
    }
  }

  /**
   * Take the entity an get all ctor parameter names and store instance mirror for later use.
   */
  private def doSetterLookup(): Future[(ru.InstanceMirror, ru.MethodSymbol, List[String])] = {
    Future {
      Entity.getReflectedApplyWArgNames[T]()(classTag)
    }
  }

  /**
   * Return reflected getter.
   */
  private def getter: Map[String, ru.MethodMirror] = Await.result(_cachedGetter, timeoutForDAOAccess)

  /**
   * Return reflected getter.
   */
  private def setter: (ru.InstanceMirror, ru.MethodSymbol, List[String]) = Await.result(_cachedSetter, timeoutForDAOAccess)

  /**
   * Load a model with the given id from model handler.
   * Can be used to provide an easy access to parent or sub model.
   */
  protected def awaitLoading[E <: Model[_]](
    future: Future[smhReadRes]
    )(implicit classTag: ClassTag[E]): Option[E] = {
    val promise = Promise[Option[E]]()

    future onComplete {
      case Success(r: ReadSuccess) if classTag.runtimeClass.isAssignableFrom(r.model.getClass) => promise success Some(r.model.asInstanceOf[E])
      case Success(r: ReadFailure)                                                             => promise success None
      case Success(_)                                                                          => promise success None
      case Failure(_)                                                                          => promise success None
    }

    Await.result(promise.future, timeoutForDAOAccess)
  }

  /**
   * Load a model with the given id from model handler.
   * Can be used to provide an easy access to parent or sub model.
   */
  protected def awaitLoading[E <: Model[_]](
    future: Option[Future[smhReadRes]]
    )(implicit classTag: ClassTag[E]): Option[E] = {
    future match {
      case Some(x) => awaitLoading[E](x)(classTag)
      case None => None
    }
  }

  /**
   * Load a model with the given id from model handler.
   * Can be used to provide an easy access to parent or sub model.
   */
  protected def loadModel(id: UUID): Future[smhReadRes] = (modelHandler ? ModelHandler.Read(id)).mapTo[smhReadRes]

  /**
   * Load a model with the given id from model handler.
   * Can be used to provide an easy access to parent or sub model.
   */
  protected def loadModel(id: Option[UUID]): Option[Future[smhReadRes]] = {
    id match {
      case Some(x) => Some(loadModel(x))
      case None => None
    }
  }

  /**
   * This function send an update request with the given entity to the model handler.
   *
   * @param newEntity is the new entity
   * @param propertyName is the changed property
   * @param oldValue is the old value
   * @param newValue is the new value
   */
  protected def update(
    newEntity: T,
    propertyName: String,
    oldValue: Any,
    newValue: Any
    ) {
    synchronized {
      if (_isDeleted) {
        fireOnDeleted(Some("This model is deleted. So please do not change something anymore"))
      } else {
        _entity = Future {
          dao.update(newEntity).asInstanceOf[T]
        }

        fireOnChanged(propertyName = propertyName, oldValue = oldValue, newValue = newValue)
      }
    }
  }

  /**
   * Async load of the underlying entity.
   */
  private def load() = {
    val promise = Promise[T]()
    val loader = Future { dao.read(entityId) }

    loader onComplete {
      case Success(Some(e: T)) => promise success e
      case Success(None)       => promise success dao.create(classTag.runtimeClass.getName).asInstanceOf[T]
      case Success(e)          => promise failure new IllegalStateException(s"entity has wrong type: $e, expected $classTag")
      case Failure(t)          => promise failure new IllegalStateException(s"loading entity leads into failure: $t")
    }

    _entity = promise.future
    _entity
  }
}
