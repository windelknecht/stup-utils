package de.windelknecht.stup.utils.coding.mvc

import scala.reflect.ClassTag

trait DaoIsCloseable {
  /**
   * Dao will be closed (and saved)
   */
  def close(): Unit
}

trait Dao {
  /**
   * Create an entity - via cached reflection.
   * The new entity is NOT added to the data set.
   */
  def create[E <: Entity]()(implicit classTag: ClassTag[E]): E = create(classTag.runtimeClass).asInstanceOf[E]

  /**
   * Create an entity - via cached reflection.
   * The new entity is NOT added to the data set.
   */
  def create(clazz: Class[_]): Entity = create(clazz.getName)

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
  def delete(id: String)

  /**
   * Search and return the entity with the given id.
   *
   * @param id id of the wanted entity
   * @return entity
   */
  def read(id: String): Option[Entity]

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
