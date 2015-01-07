package de.windelknecht.stup.utils.coding.mvc.dao

import de.windelknecht.stup.utils.coding.mvc.{Dao, Entity}

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}

class MemoryDao
  extends Dao {
  case class EntityInfo[T <: Entity](classTag: ClassTag[T], typeTag: ru.TypeTag[T])

  // fields
  private val _cache = new mutable.HashMap[String, Entity]()

  /**
   * Close data source.
   */
  override def close() = {}

  /**
   * Remove the entity with the given id from the data set.
   *
   * @param id id of the entity to remove
   */
  override def delete(id: String) = _cache.synchronized { _cache -= id }

  /**
   * Search and return the entity with the given id.
   *
   * @param id id of the wanted entity
   * @return entity
   */
  override def read(id: String) = _cache.synchronized { _cache.get(id) }

  /**
   * List all entities.
   *
   * @return list of entities
   */
  override def read() = _cache.synchronized { _cache.values.toList }

  /**
   * Updates the data set with the already existing entity (new data).
   *
   * @param entity entity to update
   */
  override def update[T <: Entity](entity: T) = {
    _cache.synchronized {
      _cache += (entity.id -> entity)
    }

    entity
  }
}
