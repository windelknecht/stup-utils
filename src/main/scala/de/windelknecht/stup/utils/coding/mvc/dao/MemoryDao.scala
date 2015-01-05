package de.windelknecht.stup.utils.coding.mvc.dao

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.{Dao, Entity}

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}

class MemoryDao
  extends Dao {
  case class EntityInfo[T <: Entity](classTag: ClassTag[T], typeTag: ru.TypeTag[T])

  // fields
  private val _cache = new mutable.HashMap[UUID, Entity]()
  private val _typeCache = new mutable.HashMap[UUID, EntityInfo[_]]()

  /**
   * Close data source.
   */
  override def close() = {}

  /**
   * Remove the entity with the given id from the data set.
   *
   * @param id id of the entity to remove
   */
  override def delete(id: UUID) = _cache.synchronized {
    _cache -= id
    _typeCache -= id
  }

  /**
   * Search and return the entity with the given id.
   *
   * @param id id of the wanted entity
   * @return entity
   */
  override def read(id: UUID) = _cache.synchronized { _cache.get(id) }

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
  override def update[T <: Entity](entity: T)(implicit classTag: ClassTag[T], typeTag: ru.TypeTag[T]) = {
    _cache.synchronized {
      _cache += (entity.id -> entity)
      _typeCache += (entity.id -> EntityInfo(classTag, typeTag))
    }

    entity
  }

  protected def getRuntimeInfo[T <: Entity](
    entity: T
    ): Option[EntityInfo[T]] = _typeCache.get(entity.id).asInstanceOf[Option[EntityInfo[T]]]
}
