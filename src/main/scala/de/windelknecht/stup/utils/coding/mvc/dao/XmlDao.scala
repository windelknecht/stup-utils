package de.windelknecht.stup.utils.coding.mvc.dao

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity
import de.windelknecht.stup.utils.coding.reflect.{ObjectReflector, CaseClassReflector}
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
class FieldClassIsUnknown(m: String) extends Exception

class XmlDao(
  file: FileObject
  )
  extends MemoryDao {
  implicit def convertToString(node: NodeSeq): String = node.text

  // fields
  private val ENTITY_LABEL = "entity"
  private val FIELD_LABEL = "field"
  private val LISTITEM_LABEL = "item"
  private val CLASSNAME_ATTR = "className"
  private val ID_ATTR = "id"
  private val TYPE_ATTR = "type"
  private val NAME_ATTR = "name"

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

    // get args
    val args = (node \ "field")
      .map { i =>
      val name = (i \ "@name").text
      val tpe = (i \ "@type").text
      val value = i.text

      tpe match {
        case e if e == classOf[java.lang.Byte].getName      => java.lang.Integer.parseInt(value, 10).toByte
        case e if e == classOf[java.lang.Character].getName => value.charAt(0)
        case e if e == classOf[java.lang.Double].getName    => java.lang.Double.parseDouble(value)
        case e if e == classOf[java.lang.Float].getName     => java.lang.Float.parseFloat(value)
        case e if e == classOf[java.lang.Integer].getName   => java.lang.Integer.parseInt(value, 10)
        case e if e == classOf[java.lang.Long].getName      => java.lang.Long.parseLong(value)

        case e if e == classOf[String].getName => value
        case e if e == classOf[UUID].getName   => UUID.fromString(value)

        case e@_ => throw new FieldClassIsUnknown(s"field '$name' if this node (${new PrettyPrinter(80,4).format(node)}) has unknown type")
      }
    }

    val entity = try {
      Entity.create(className, args:_*)
    } catch {
      case e: Exception => throw new EntityClassIsUnknown(s"problems to deSerialize the following node // ${new PrettyPrinter(80, 4).format(node)} (${e.getMessage}})")
    }

    entity
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
    <a>{ serializeFields(entity) }</a>.copy(label = ENTITY_LABEL) % Attribute(CLASSNAME_ATTR, Text(entity.getClass.getName), Null)
  }

  /**
   * Serialize all entity fields.
   */
  private def serializeFields(
    entity: Entity
    ): NodeSeq = {
    CaseClassReflector
      .reflectApplyValues(entity)
      .map { case(name,value) =>
      value match {
        case head :: tail => serializeList(entity, name, List(head) ++ tail)
        case _            => <a>{value}</a>.copy(label = FIELD_LABEL) % Attribute(NAME_ATTR, Text(name), Null) % Attribute(TYPE_ATTR, Text(value.getClass.getName), Null)
      }
    }
  }

  /**
   * Serialize a complete list.
   */
  private def serializeList(
    entity: Entity,
    name: String,
    value: List[Any]
    ): Node = {
    ObjectReflector
      .reflectTypeByMemberName(entity, name) match {
      case Some(x) => <a>{ value.map { i => <b>{i}</b>.copy(label = LISTITEM_LABEL) } }</a>.copy(label = FIELD_LABEL) % Attribute(NAME_ATTR, Text(name), Null) % Attribute(TYPE_ATTR, Text(x.toString), Null)
      case None => throw new FieldClassIsUnknown(s"unknown type of field '$name' of this object: '${entity.toString}'")
    }
  }
}
