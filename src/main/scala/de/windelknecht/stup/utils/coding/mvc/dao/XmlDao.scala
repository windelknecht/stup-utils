package de.windelknecht.stup.utils.coding.mvc.dao

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity
import de.windelknecht.stup.utils.io.vfs._
import org.apache.commons.vfs2.FileObject

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import scala.xml.XML

class XmlDao(
  file: FileObject
  )
  extends MemoryDao {
  // fields
  private val _initDone = Promise[Boolean]()

  // ctor
  // TODO: make it async
  init()

  /**
   * Returns true, if the complete file is read completely.
   * All crud ops will be delayed.
   */
  def isReady = _initDone.isCompleted

  /**
   * Close data source.
   */
  override def close() = {
    _initDone.failure(new IllegalArgumentException(s"init is not done yet"))

    // close file
    file.synchronized {
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
  override def update[T <: Entity](entity: T) = {
    waitForInitDone()
    super.update(entity)
  }

  /**
   * Block until init is done.
   */
  def waitForInitDone(): Unit = Await.ready(_initDone.future, Duration.Inf)

  /**
   * Does a complete file read.
   */
  private def init(): Unit = {
    // read complete file into cache
    val node = XML.load(file.read().reader())

    (node \ "entity").map { c =>

    }

    _initDone.success(true)
  }
}
