package de.windelknecht.stup.utils.coding.mvc.dao

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity
import de.windelknecht.stup.utils.coding.mvc.entities.{Entity_moreSimple, Entity_onlyId}
import de.windelknecht.stup.utils.io.vfs._
import de.windelknecht.stup.utils.tools.RandomHelper
import org.apache.commons.vfs2.{FileObject, VFS}
import org.scalatest.{Matchers, WordSpec}

import scala.xml._

class XmlDaoSpec
  extends WordSpec
  with Matchers {
  implicit def convertToString(node: NodeSeq): String = node.text

  "init a XmlDao class" when {
    "given file is invalid" should {
      "don't throw an exception if file does not exist" in {
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")

        new XmlDao(file)
      }

      "entity size is 0, if file does not exist" in {
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")

        new XmlDao(file).read().size should be(0)
      }

      "don't throw an exception if file is empty" in {
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")

        file.createFile()
        new XmlDao(file)
      }

      "entity size is 0, if file is empty" in {
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")

        file.createFile()
        new XmlDao(file).read().size should be(0)
      }

      "throw an exception if file is a dir" in {
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")

        file.createFolder()
        an[NotAFileException] should be thrownBy { new XmlDao(file) }
      }

      "throw an exception if file is not writeable" in {
        val file = VFS.getManager.resolveFile("/etc/fstab")

        file.isWriteable should be (right = false)
        an[NotWritableException] should be thrownBy { new XmlDao(file) }
      }

      "throw an exception if file contains invalid content" in {
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")

        file.write(RandomHelper.rndString())
        an[FileContentInvalidException] should be thrownBy { new XmlDao(file) }
      }
    }

    "given file is valid (1 entity)" should {
      "have an entity count of 1 after init" in {
        val (file, ent) = create_file()

        val dao = new XmlDao(file)
        dao.read().size should be (1)
      }

      "should be the same" in {
        val (file, ent) = create_file()

        new XmlDao(file).read(ent.id) should be (Some(ent))
      }

      def create_file(): (FileObject, Entity_onlyId) = {
        val ent = Entity_onlyId()
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")
        val xml = <root>
          { <entity/> % Attribute("id", Text(ent.id.toString), Null) % Attribute("className", Text(ent.getClass.getName), Null) }
        </root>

        file.createFile()
        file.write(new PrettyPrinter(200, 4).format(xml))
        file.close()

        (file, ent)
      }
    }

    "given file is valid (more entities)" should {
      "throw Exception, when unknown classname" in {
        val (file, ent) = create_file_moreEntities_classNameWrong()

        an[EntityClassIsUnknown] should be thrownBy { new XmlDao(file) }
      }

      "have a correct entity count after init" in {
        val (file, ent) = create_file_moreEntities()

        val dao = new XmlDao(file)
        dao.read().size should be (ent.size)
      }

      "should be the same" in {
        val (file, ent) = create_file_moreEntities()

        ent.foreach { e =>
          withClue(s"entity no equal") {
            new XmlDao(file).read(e.id) should be(Some(e))
          }
        }
      }

      def create_file_moreEntities(): (FileObject, List[Entity]) = {
        val ent = List(Entity_onlyId())
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")
        val xml = <root>
          { ent.map { case e: Entity_onlyId => <entity/> % Attribute("id", Text(e.id.toString), Null) % Attribute("className", Text(e.getClass.getName), Null) }}
        </root>

        file.createFile()
        file.write(new PrettyPrinter(200, 4).format(xml))
        file.close()

        (file, ent)
      }

      def create_file_moreEntities_classNameWrong(): (FileObject, List[Entity]) = {
        val ent = List(Entity_onlyId())
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")
        val xml = <root>
          { ent.map { case e: Entity_onlyId => <entity/> % Attribute("id", Text(e.id.toString), Null) % Attribute("className", Text(ent.getClass.getName), Null) }}
        </root>

        file.createFile()
        file.write(new PrettyPrinter(200, 4).format(xml))
        file.close()

        (file, ent)
      }
    }
  }

  "close a XmlDao object" when {
    "having no entities" should {
      "entity node count == 0" in {
        val (dao, file) = createDao()
        dao.close()

        (XML.load(file.read().reader()) \ "entity").size should be (0)
      }
    }

    "having 1 entity (Entity_onlyId, no fields)" should {
      "entity node count == 1" in {
        val (dao, file) = createDao(Entity_onlyId())
        dao.close()

        (XML.load(file.read().reader()) \ "entity").size should be (1)
      }

      "entity node has correct attribute 'className'" in {
        (createAndClose_Entity_onlyId()._1 \ "@className").text should be (classOf[Entity_onlyId].getName)
      }

      "entity node has correct attribute 'id'" in {
        val (node, ent) = createAndClose_Entity_onlyId()
        UUID.fromString(node \ "@id") should be (ent.id)
      }

      "entity node has no text content" in {
        val (node, ent) = createAndClose_Entity_onlyId()
        node.text should be ("")
      }

      def createAndClose_Entity_onlyId(): (Node, Entity_onlyId) = {
        val ent = Entity_onlyId()
        val (dao, file) = createDao(ent)
        dao.close()

        ((XML.load(file.read().reader()) \ "entity")(0), ent)
      }
    }

    "having 1 entity (Entity_more, various fields)" should {
      "entity node count == 1" in {
        val (dao, file) = createDao(Entity_moreSimple())
        dao.close()

        (XML.load(file.read().reader()) \ "entity").size should be (1)
      }

      "entity node has correct attribute 'className'" in {
        (createAndClose_Entity_moreSimple()._1 \ "@className").text should be (classOf[Entity_moreSimple].getName)
      }

      "entity node has correct attribute 'id'" in {
        val (node, ent) = createAndClose_Entity_moreSimple()
        UUID.fromString(node \ "@id") should be (ent.id)
      }

      "entity node has correct child count" in {
        val (node, ent) = createAndClose_Entity_moreSimple()
        createAndClose_Entity_moreSimple()._1.child.size should be (2)
      }

      def createAndClose_Entity_moreSimple(): (Node, Entity_moreSimple) = {
        val ent = Entity_moreSimple()
        val (dao, file) = createDao(ent)
        dao.close()

        ((XML.load(file.read().reader()) \ "entity")(0), ent)
      }
    }
  }

  /**
   * Create a dao with 1 entity.
   */
  private def createDao(entities: Entity*): (XmlDao, FileObject) = {
    val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")

    file.createFile()
    val dao = new XmlDao(file)

    entities.foreach(dao.update)
    (dao, file)
  }
}
