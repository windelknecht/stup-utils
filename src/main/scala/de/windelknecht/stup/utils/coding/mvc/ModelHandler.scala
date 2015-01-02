package de.windelknecht.stup.utils.coding.mvc

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import de.windelknecht.stup.utils.coding.EventEntpreller.{EventDescr, OnTimerExpired}
import de.windelknecht.stup.utils.coding.akka.ChainActor
import de.windelknecht.stup.utils.coding.mvc.Model.SM
import Select.{SelectFn, SelectRes}
import de.windelknecht.stup.utils.coding.reactive.Notify
import de.windelknecht.stup.utils.coding.reactive.Notify._
import de.windelknecht.stup.utils.coding.{EventEntpreller, StupMessages}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.existentials

object ModelHandler {
  trait smhEvent extends NotifyEvent

  /**
   * Is fired when a new model was created or an creation error occurred.
   * Returns a CreateSuccess(.) message.
   */
  case object OnModelCreated extends smhEvent

  /**
   * Is fired when a model was deleted.
   * Returns a DeleteSuccess(.) message.
   */
  case object OnModelDeleted extends smhEvent

  /**
   * Is fired when a single model was read.
   * Returns a ReadSuccess(.) message.
   */
  case object OnModelRead    extends smhEvent

  /**
   * Is fired when an error occurred while creating, reading or deleting a model.
   * Returns either a CreateFailure(.) or a ReadFailure(.) message.
   */
  case object OnFailure      extends smhEvent

  trait smhMsg

  trait smhReq extends smhMsg
  case class Create  (className: String) extends smhReq
  case class Read    (id: UUID)          extends smhReq
  case class ReadMore(select: SelectFn)  extends smhReq
  case class Delete  (id: UUID)          extends smhReq

  trait smhRes extends smhMsg
  trait smhCreateRes   extends smhRes
  trait smhDeleteRes   extends smhRes
  trait smhReadRes     extends smhRes
  trait smhReadMoreRes extends smhRes

  case class CreateSuccess(model: SM)             extends smhCreateRes
  case class CreateFailure(err: String)           extends smhCreateRes

  case class DeleteSuccess(id: UUID)              extends smhDeleteRes
  case class DeleteFailure(id: UUID, err: String) extends smhDeleteRes

  case class ReadSuccess(model: SM)               extends smhReadRes
  case class ReadFailure(id: UUID, err: String)   extends smhReadRes

  case class ReadMoreSuccess(select: SelectRes)   extends smhReadMoreRes
  case class ReadMoreFailure(err: String)         extends smhReadMoreRes

  /**
   * Create a actor reference.
   */
  def create(
    dao: Dao,
    nameSpaceForModelSearch: String
    )(implicit actorSystem: ActorSystem) = actorSystem.actorOf(props(dao, nameSpaceForModelSearch))

  /**
   * Create props.
   */
  def props(
    dao: Dao,
    nameSpaceForModelSearch: String,
    gcCleanUpTimeInMs: Int = 5000
    ) = Props(classOf[ModelHandler], dao, nameSpaceForModelSearch, gcCleanUpTimeInMs)
}

class ModelHandler(
  dao: Dao,
  nameSpaceForModelSearch: String,
  gcCleanUpTimeInMs: Int
  )
  extends ChainActor
  with Notify {
  import de.windelknecht.stup.utils.coding.mvc.ModelHandler._
  
  case object GCEvent
  case class GCObject(timeStamp: Long, model: SM)

  implicit val actorSystem = context.system

  // fields
  private val _cachedModels = new mutable.HashMap[UUID, SM]()
  protected val _uuid = UUID.randomUUID()
  private val _waitForGC = new mutable.HashMap[UUID, GCObject]()
  private val _gc = new EventEntpreller(
    Map(GCEvent -> (1 seconds)),
    notifier = {
      case (OnTimerExpired, EventDescr(GCEvent, _)) ++ _ => doGC()
    }
  )

  // ctor
  receiver(notifyReceive)
  Model.searchHereForModels(nameSpaceForModelSearch)

  /**
   * Take this entity and make a stup model.
   */
  def mapAsModel(entity: Entity): Option[SM] = createModel(entity)

  /**
   * This method will be called, when a shutdown msg is received.
   */
  protected def shutdown() = {
    _gc.shutdown()

    context stop self
  }

  /**
   * Sub classes must implement this msg handler method.
   */
  receiver {
    case Create(className) => create(sender(), className)
    case Read(id)          => read(sender(), id)
    case ReadMore(fn)      => readMore(sender(), fn)
    case Delete(id)        => delete(sender(), id)

    case StupMessages.Shutdown => shutdown()

    case _ =>
  }

  /**
   * Do a create request.
   * The code is enclosed in a future.
   */
  protected def create(
    sender: ActorRef,
    className: String
    ) {
    Future {
       val msg = try {
        createModel(dao.create(className)) match {
          case Some(x) => CreateSuccess(x)
          case None    => CreateFailure(s"stup model for this entity $className not registered. Either it is not defined, not within given namespace ($nameSpaceForModelSearch) or has no matching ctor. (Please see logging output)")
        }
      } catch {
        case e: ClassNotFoundException => CreateFailure(s"could not create an entity of type $className. (Got: ${e.getMessage}})")
      }

      sender ! msg

      msg match {
        case m: CreateSuccess => fireNotify(OnModelCreated, m)
        case m: CreateFailure => fireNotify(OnFailure, m)
      }
    }
  }

  /**
   * Do a delete request.
   * The code is enclosed in a future.
   */
  protected def delete(
    sender: ActorRef,
    id: UUID
    ) {
    Future {
      id.synchronized {
        dao.delete(id)

        // notify stup model
        _cachedModels.get(id) match {
          case Some(x) => x.youAreDeleted()
          case None    =>
            _waitForGC.get(id) match {
              case Some(x) => x.model.youAreDeleted()
              case None    =>
            }
        }
      }

      val msg = DeleteSuccess(id)

      sender ! msg
      fireNotify(OnModelDeleted, msg)
    }
  }

  /**
   * Do a read request.
   * The code is enclosed in a future.
   */
  protected def read(
    sender: ActorRef,
    id: UUID
    ) {
    Future {
      val msg = read(id)

      sender ! msg
      fireNotify(OnModelRead, msg)

      msg match {
        case m: ReadSuccess => fireNotify(OnModelRead, m)
        case m: ReadFailure => fireNotify(OnFailure, m)
      }
    }
  }

  /**
   * Do a read request.
   * The code is enclosed in a future.
   */
  protected def readMore(
    sender: ActorRef,
    fn: SelectFn
    ) {
    Future {
      sender ! ReadMoreSuccess(fn(dao.read())(this))
    }
  }

  /**
   * Funktion zum Erstellen eines Models und zum cachen desselben.
   */
  private def createModel(
    entity: Entity
    ): Option[Model[_ <: Entity]] = {
    Model.createModel(entity, dao, self) match {
      case Some(x) => Some(initModel(entity, x))
      case None => None
    }
  }

  /**
   * Cleanup unused stup model controllers.
   */
  private def doGC() {
    _waitForGC.synchronized {
      val toGC = _waitForGC.filter{case(_, GCObject(time, _)) => isGCWaitExpired(time) }

      toGC.foreach{ case(id, GCObject(_, sm)) =>
        _waitForGC.remove(id)

        sm.unregisterNotify(_uuid)
      }
    }
  }

  /**
   * Funktion zum Erstellen eines Models und zum cachen desselben.
   */
  private def initModel(
    entity: Entity,
    sm: Model[_ <: Entity]
    ): Model[_ <: Entity] = {
    // listener von beginn an in _waitForGC einsortieren, mit timestamp und dann nach ..s aufräumen, der kommt automatisch nach cached, wenn er genutzt wird
    _waitForGC.synchronized {
      _waitForGC += (entity.id -> GCObject(System.currentTimeMillis(), sm))
    }
    _gc.entprell(GCEvent, null)

    sm.registerNotify(_uuid, {
      case (OnListenerChanged, HasListenerAdded)   ++ _ if _waitForGC.contains(sm.id)      => moveFromGCList(sm)
      case (OnListenerChanged, HasListenerRemoved) ++ _ if sm.registeredListenerCount == 1 => moveToGCList(sm)
    })

    sm.asInstanceOf[Model[_ <: Entity]]
  }

  /**
   * Returns true is the sup model controller is now unused for about x seconds.
   */
  private def isGCWaitExpired(time: Long): Boolean = (System.currentTimeMillis() - time) > gcCleanUpTimeInMs

  /**
   * Remove the given stup model from gc list.
   */
  private def moveFromGCList(
    sm: Model[_ <: Entity]
    ) {
    sm.id.synchronized {
      _waitForGC.synchronized {
        _waitForGC -= sm.id
      }
      _cachedModels.synchronized {
        _cachedModels += (sm.id -> sm)
      }
    }
  }

  /**
   * Move the given stup model onto the gc list.
   */
  private def moveToGCList(
    sm: Model[_ <: Entity]
    ) {
    sm.id.synchronized {
      _cachedModels.synchronized {
        _cachedModels -= sm.id
      }
      _waitForGC.synchronized {
        _waitForGC += (sm.id -> GCObject(System.currentTimeMillis(), sm))
      }
      _gc.entprell(GCEvent, null)
    }
  }

  /**
   * Mit dieser Funktion soll eine Entity mit der gegebenen id gelesen werden, damit ein Model erstellt und
   * dieses zurückgegeben und gecacht werden.
   */
  private def prefetchRead(
    id: UUID
    ): smhReadRes = {
    id.synchronized {
      dao.read(id) match {
        case Some(x) =>
          Model.createModel(x, dao, self) match {
            case Some(xx) => ReadSuccess(xx)
            case None     => ReadFailure(id, err = s"stup model for this entity not registered. Either it is not defined, not within given namespace ($nameSpaceForModelSearch) or has no matching ctor. (Please see logging output)")
          }

        case _            => ReadFailure(id, err = s"could not read entity with id $id")
      }
    }
  }

  /**
   * Request to return a stup model with an entity (id) underlying.
   * If the stup model is cached, it will be delivered, otherwise a new one is created (and cached).
   */
  private def read(id: UUID) = {
    _waitForGC.get(id)

    _cachedModels.get(id) match {
      case Some(x) => ReadSuccess(x)
      case None    =>
        _waitForGC.get(id) match {
          case Some(x) =>
            moveFromGCList(x.model)
            ReadSuccess(x.model)
          case None    => prefetchRead(id)
        }
    }
  }
}

