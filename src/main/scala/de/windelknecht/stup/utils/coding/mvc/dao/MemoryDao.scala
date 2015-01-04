package de.windelknecht.stup.utils.coding.mvc.dao

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.{Dao, Entity}

import scala.collection.mutable

class MemoryDao
  extends Dao {
  // fields
  private val _cache = new mutable.HashMap[UUID, Entity]()

  /**
   * Close data source.
   */
  override def close() = {}

  /**
   * Remove the entity with the given id from the data set.
   *
   * @param id id of the entity to remove
   */
  override def delete(id: UUID) = _cache -= id

  /**
   * Search and return the entity with the given id.
   *
   * @param id id of the wanted entity
   * @return entity
   */
  override def read(id: UUID) = _cache.get(id)

  /**
   * List all entities.
   *
   * @return list of entities
   */
  override def read() = _cache.values.toList

  /**
   * Updates the data set with the already existing entity (new data).
   *
   * @param entity entity to update
   */
  override def update[T <: Entity](entity: T) = {
    _cache += (entity.id -> entity)
    entity
  }
}
