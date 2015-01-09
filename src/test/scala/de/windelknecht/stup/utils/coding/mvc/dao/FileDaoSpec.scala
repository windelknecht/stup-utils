package de.windelknecht.stup.utils.coding.mvc.dao

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity
import de.windelknecht.stup.utils.coding.mvc.dao.serialize.{JsonDaoDeSerializer, JsonDaoSerializer}
import de.windelknecht.stup.utils.coding.mvc.entities.{Entity_moreListString, Entity_moreComplex, Entity_moreSimple, Entity_onlyId}
import de.windelknecht.stup.utils.tools.RandomHelper
import org.apache.commons.vfs2.{VFS, FileObject}
import org.scalatest.{Matchers, WordSpec}

class JsonDao(
  file: FileObject
  ) extends FileDao(file = file, serializer = new JsonDaoSerializer, deSerializer = new JsonDaoDeSerializer)

class FileDaoSpec
  extends WordSpec
  with Matchers {
  "JsonDao after saving" when {
    "open a saved file" should {
      "parse correct entity count" in {
        val (dao, file, list) = createDaoAndAddEntities()
        dao.close()

        new JsonDao(file).read().size should be (list.size)
      }

      "parse correct entities" in {
        val (dao, file, list) = createDaoAndAddEntities()
        dao.close()

        val tto = new JsonDao(file)
        list.foreach { i =>
          tto.read(i.id) match {
            case Some(e: Entity_onlyId)         => compare_Entity_onlyId(i.asInstanceOf[Entity_onlyId], e)
            case Some(e: Entity_moreSimple)     => compare_Entity_moreSimple(i.asInstanceOf[Entity_moreSimple], e)
            case Some(e: Entity_moreComplex)    => compare_Entity_moreComplex(i.asInstanceOf[Entity_moreComplex], e)
            case Some(e: Entity_moreListString) => compare_Entity_moreListString(i.asInstanceOf[Entity_moreListString], e)

            case _ => fail(s"unknown datatype '$i'")
          }

          tto.read(i.id).get should be (i)
        }
      }

      def compare_Entity_onlyId(
        _this: Entity_onlyId,
        _that: Entity_onlyId
        ): Unit = {
        withClue(s"id of '${_this}' doesnt match") { _that.id should be (_this.id) }
      }

      def compare_Entity_moreSimple(
        _this: Entity_moreSimple,
        _that: Entity_moreSimple
        ): Unit = {
        withClue(s"id of '${_this}' doesnt match") { _that.id should be (_this.id) }
        withClue(s"name of '${_this}' doesnt match") { _that.name should be (_this.name) }
        withClue(s"age of '${_this}' doesnt match") { _that.age should be (_this.age) }
      }

      def compare_Entity_moreComplex(
        _this: Entity_moreComplex,
        _that: Entity_moreComplex
        ): Unit = {
        withClue(s"if of '${_this}' doesnt match") { _that.id should be (_this.id) }
        withClue(s"v01 of '${_this}' doesnt match") { _that.v01 should be (_this.v01) }
//        withClue(s"v02 of '${_this}' doesnt match") { _that.v02 should be (_this.v02) }
        withClue(s"v03 of '${_this}' doesnt match") { _that.v03 should be (_this.v03) }
//        withClue(s"v04 of '${_this}' doesnt match") { _that.v04 should be (_this.v04) }
        withClue(s"v05 of '${_this}' doesnt match") { _that.v05 should be (_this.v05) }
//        withClue(s"v06 of '${_this}' doesnt match") { _that.v06 should be (_this.v06) }
        withClue(s"v07 of '${_this}' doesnt match") { _that.v07 should be (_this.v07) }
//        withClue(s"v08 of '${_this}' doesnt match") { _that.v08 should be (_this.v08) }
        withClue(s"v09 of '${_this}' doesnt match") { _that.v09 should be (_this.v09) }
//        withClue(s"v10 of '${_this}' doesnt match") { _that.v10 should be (_this.v10) }
        withClue(s"v11 of '${_this}' doesnt match") { _that.v11 should be (_this.v11) }
        withClue(s"v12 of '${_this}' doesnt match") { _that.v12 should be (_this.v12) }
      }

      def compare_Entity_moreListString(
        _this: Entity_moreListString,
        _that: Entity_moreListString
        ): Unit = {
        withClue(s"id of '${_this}' doesnt match") { _that.id should be (_this.id) }
        withClue(s"list of '${_this}' doesnt match") { _that.list should be (_this.list) }
      }
    }
  }

  /**
   * Create a dao with given entities.
   */
  private def createDaoAndAddEntities(): (JsonDao, FileObject, List[Entity]) = {
    val content = (
         (1 to RandomHelper.rndInt(upper = 10)).map { i => Entity_onlyId() }
      ++ (1 to RandomHelper.rndInt(upper = 10)).map { i => Entity_moreSimple(name = RandomHelper.rndString(), age = RandomHelper.rndInt()) }
      ++ (1 to RandomHelper.rndInt(upper = 10)).map { i =>
           Entity_moreComplex(
             v01 = RandomHelper.rndInt().toByte,
//             v02 = RandomHelper.rndInt().toByte,
             v03 = RandomHelper.rndString(1).charAt(0),
//             v04 = RandomHelper.rndString(1).charAt(0),
             v05 = RandomHelper.rndDouble,
//             v06 = RandomHelper.rndDouble,
             v07 = RandomHelper.rndFloat,
//             v08 = RandomHelper.rndFloat,
             v09 = RandomHelper.rndInt(),
//             v10 = RandomHelper.rndInt(),
             v11 = RandomHelper.rndString(10),
             v12 = RandomHelper.rndString(10)
           )
      }
      ++ (1 to RandomHelper.rndInt(upper = 10)).map { i => Entity_moreListString(list = (1 to RandomHelper.rndInt(upper = 10)).map { i => RandomHelper.rndString() }.toList) }
    )
    val file = VFS.getManager.resolveFile(s"ram://XmlJsonSpec/${UUID.randomUUID()}.xml")

    file.createFile()
    val dao = new JsonDao(file)

    content.foreach(dao.update)
    (dao, file, content.toList)
  }
}
