package de.windelknecht.stup.utils.coding.mvc

import de.windelknecht.stup.utils.coding.mvc.Model.SM

import scala.reflect.ClassTag

object Select {
  implicit class FromList(
    list: List[Entity]
    )(implicit smh: ModelHandler) {
    /**
     * Generate from the entity list an list of all ids.
     */
    def mapAsIds: List[String] = list.map(_.id)

    /**
     * Generate from the entity list an list with stup models.
     */
    def mapAsModels: List[_ <: SM] = list.map(smh.mapAsModel).filterNot(_ == None).map(_.get)
  }

  trait SelectRes
  case class CountRes    (value:Int)            extends SelectRes
  case class IdListRes   (value: List[String])  extends SelectRes
  case class ModelListRes(value: List[_ <: SM]) extends SelectRes

  type SelectFn = List[Entity] => ModelHandler => SelectRes

  /**
   * Predefined select func to return all available entities as List[StupModel]
   */
  def count: SelectFn = {
    implicit entities: List[Entity] =>
    implicit smh: ModelHandler =>

    CountRes(entities.size)
  }

  /**
   * Predefined select func to return all available entities as List[StupModel]
   */
  def listAll: SelectFn = {
    implicit entities: List[Entity] =>
    implicit smh: ModelHandler =>

    IdListRes(value = entities.mapAsIds)
  }

  /**
   * Predefined select func to return all available entities of this type as List[StupModel]
   */
  def listByClassName(
    className: String
    ): SelectFn = {
    implicit entities: List[Entity] =>
    implicit smh: ModelHandler =>

    IdListRes(
      value = entities
        .filter(_.getClass.getName == className)
        .mapAsIds
    )
  }

  /**
   * Predefined select func to return all available entities of this type as List[StupModel]
   */
  def listByType[T](implicit classTag: ClassTag[T]): SelectFn = {
    implicit entities: List[Entity] =>
    implicit smh: ModelHandler =>

    mapByClassName(classTag.runtimeClass.getName)(entities)(smh)
  }

  /**
   * Predefined select func to return all available entities as List[StupModel]
   */
  def mapAll: SelectFn = {
    implicit entities: List[Entity] =>
    implicit smh: ModelHandler =>

    ModelListRes(value = entities.mapAsModels)
  }

  /**
   * Predefined select func to return all available entities of this type as List[StupModel]
   */
  def mapByClassName(
    className: String
    ): SelectFn = {
    implicit entities: List[Entity] =>
    implicit smh: ModelHandler =>

    ModelListRes(
      value = entities
        .filter(_.getClass.getName == className)
        .mapAsModels
    )
  }

  /**
   * Predefined select func to return all available entities of this type as List[StupModel]
   */
  def mapByType[T](implicit classTag: ClassTag[T]): SelectFn = {
    implicit entities: List[Entity] =>
    implicit smh: ModelHandler =>

    mapByClassName(classTag.runtimeClass.getName)(entities)(smh)
  }
}
