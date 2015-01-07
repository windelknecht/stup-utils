package de.windelknecht.stup.utils.coding.mvc.dao

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity
import de.windelknecht.stup.utils.coding.mvc.entities.{Entity_moreListString, Entity_moreComplex, Entity_moreSimple, Entity_onlyId}
import de.windelknecht.stup.utils.io.vfs._
import de.windelknecht.stup.utils.tools.RandomHelper
import org.apache.commons.vfs2.{VFS, FileObject}
import org.scalatest.{Matchers, WordSpec}

class JsonDaoSpec
  extends WordSpec
  with Matchers {
  "JsonDao after saving" when {
    "open a saved file" should {
      "parse correct entity count" in {
        val (dao, file, list) = createDaoAndAddEntities()
        dao.close()

        val r1 = file.read().mkString

        val tto = new JsonDao(file)

        tto.read().size should be (list.size)
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
             v02 = RandomHelper.rndInt().toByte,
             v03 = RandomHelper.rndInt().toChar,
             v04 = RandomHelper.rndInt().toChar,
             v05 = RandomHelper.rndDouble,
             v06 = RandomHelper.rndDouble,
             v07 = RandomHelper.rndFloat,
             v08 = RandomHelper.rndFloat,
             v09 = RandomHelper.rndInt(),
             v10 = RandomHelper.rndInt(),
             v11 = RandomHelper.rndString(),
             v12 = RandomHelper.rndString()
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
