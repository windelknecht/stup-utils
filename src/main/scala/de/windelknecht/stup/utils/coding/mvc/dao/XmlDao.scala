package de.windelknecht.stup.utils.coding.mvc.dao

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity
import de.windelknecht.stup.utils.coding.reflect.CaseClassReflector
import de.windelknecht.stup.utils.io.vfs._
import org.apache.commons.vfs2.{FileType, FileObject}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import scala.reflect.ClassTag
import scala.reflect.runtime.{currentMirror => cm, universe => ru}
import scala.xml._

class NotAFileException(m: String) extends Exception
class NotWritableException(m: String) extends Exception
class FileContentInvalidException(m: String) extends Exception
class EntityClassIsUnknown(m: String) extends Exception
class EntityTypeIsUnknown(m: String) extends Exception

class XmlDao(
  file: FileObject
  )
  extends MemoryDao {
  implicit def convertToString(node: NodeSeq): String = node.text

  // fields
  private val ENTITY_LABEL = "entity"
  private val CLASSNAME_ATTR = "className"
  private val ID_ATTR = "id"

  private val _initDone = Promise[String]()

  // ctor
  // TODO: make it async
  init()
  _initDone.success("ok")

  /**
   * Returns true, if the complete file is read completely.
   * All crud ops will be delayed.
   */
  def isReady = _initDone.isCompleted

  /**
   * Close data source.
   */
  override def close() = {
    if (!_initDone.isCompleted)
      _initDone.failure(new IllegalArgumentException(s"init is not done yet"))

    // close file
    file.synchronized {
      file.write(new PrettyPrinter(500, 4).format(serialize()))
      file.close()
    }

    super.close()
  }

  /**
   * Create an entity - via cached reflection.
   * The new entity is NOT added to the data set.
   */
  override def create(className: String) = {
    waitForInitDone()
    super.create(className)
  }

  /**
   * Remove the entity with the given id from the data set.
   *
   * @param id id of the entity to remove
   */
  override def delete(id: UUID) = {
    waitForInitDone()
    super.delete(id)
  }

  /**
   * Search and return the entity with the given id.
   *
   * @param id id of the wanted entity
   * @return entity
   */
  override def read(id: UUID) = {
    waitForInitDone()
    super.read(id)
  }

  /**
   * List all entities.
   *
   * @return list of entities
   */
  override def read() = {
    waitForInitDone()
    super.read()
  }

  /**
   * Updates the data set with the already existing entity (new data).
   *
   * @param entity entity to update
   */
  override def update[T <: Entity](entity: T)(implicit classTag: ClassTag[T], typeTag: ru.TypeTag[T]) = {
    waitForInitDone()
    super.update(entity)
  }

  /**
   * Block until init is done.
   */
  def waitForInitDone(): Unit = Await.ready(_initDone.future, Duration.Inf)

  /**
   * Does a complete file read and read complete file into cache
   */
  private def init(): Unit = {
    if (!file.exists())
      return

    if (file.getType != FileType.FILE)
      throw new NotAFileException(s"given file '$file' is not of type file (${file.getType}})")

    if (!file.isWriteable)
      throw new NotWritableException(s"given file '$file' is not writeable")

    if (file.isEmpty)
      return

    val node = try {
      XML.load(file.read().reader())
    } catch {
      case e: Exception => throw new FileContentInvalidException(s"given file '$file' has invalid content (${e.getMessage}})")
    }

    deSerialize(node)
      .foreach(super.update)
  }

  /**
   * DeSerialize all entities.
   */
  private def deSerialize(node: Node): List[Entity] = (node \ "entity").map(deSerializeEntity).toList

  /**
   * DeSerialize one entity.
   */
  private def deSerializeEntity(node: Node): Entity = {
    val className: String = node \ "@className"
    val id = UUID.fromString(node \ "@id")

    val entity = try {
      Entity.create(className, id)
    } catch {
      case e: Exception => throw new EntityClassIsUnknown(s"problems to deSerialize the following node // ${new PrettyPrinter(80, 4).format(node)} (${e.getMessage}})")
    }

    entity
  }

  /**
   * Return all members of the given object.
   *
   * @param data is the object to discover
   * @param classTag implicit classtag (used for reflection)
   * @tparam T type parameter
   * @return class members
   */
  private def reflectedMembersFromObject[T](
    data: T
    )(implicit classTag: ClassTag[T]): (ru.InstanceMirror, ru.MemberScope) = {
    val typeMirror = ru.runtimeMirror(data.getClass.getClassLoader)
    val instanceMirror = typeMirror.reflect(data)
    val members = instanceMirror.symbol.typeSignature.members

    (instanceMirror, members)
  }

  /**
   * Serialize complete content into xml.
   */
  private def serialize(): Node = <root>{ read().map(serializeEntity) }</root>

  /**
   * Serialize this entity.
   */
  private def serializeEntity(
    entity: Entity
    ): Node = {
    <a>{ serializeFields(entity) }</a>.copy(label = ENTITY_LABEL) % Attribute(CLASSNAME_ATTR, Text(entity.getClass.getName), Null) % Attribute(ID_ATTR, Text(entity.id.toString), Null)
  }

  /**
   * Serialize all entity fields.
   */
  private def serializeFields(
    entity: Entity
    ): NodeSeq = {
//    val entityInfo = super.getRuntimeInfo(entity)
//
//    if(entityInfo.isEmpty)
//      throw new EntityTypeIsUnknown(s"could not serialize $entity, because I have no type info")
//
    val (instanceMirror, members) = reflectedMembersFromObject(entity)

    val r1 = members.filter(_.isMethod)


    val (im, mApply, smys) = CaseClassReflector.reflectApplyAndArgNames(entity.getClass)

    // map values and names

    NodeSeq.Empty
  }
}
