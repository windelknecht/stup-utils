package de.windelknecht.stup.utils.coding.mvc

import java.util.UUID

trait Dao {
  /**
   * Close data source.
   */
  def close()

  /**
   * Create an entity - via cached reflection.
   * The new entity is NOT added to the data set.
   */
  def create(className: String): Entity = Entity.create(className)

  /**
   * Remove the entity with the given id from the data set.
   *
   * @param id id of the entity to remove
   */
  def delete(id: UUID)

  /**
   * Search and return the entity with the given id.
   *
   * @param id id of the wanted entity
   * @return entity
   */
  def read(id: UUID): Option[Entity]

  /**
   * List all entities.
   *
   * @return list of entities
   */
  def read(): List[Entity]

  /**
   * Updates the data set with the already existing entity (new data).
   *
   * @param entity entity to update
   */
  def update[T <: Entity](entity: T): Entity
}
