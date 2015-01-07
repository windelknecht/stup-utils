package de.windelknecht.stup.utils.coding.mvc.dao

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity
import de.windelknecht.stup.utils.coding.mvc.entities._
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
          { <entity><field name="id" type="java.lang.String">{ ent.id }</field></entity> % Attribute("className", Text(ent.getClass.getName), Null) }
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
        val ent = List(Entity_onlyId(), Entity_onlyId())
        val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")
        val xml = <root>
          { ent.map { case e: Entity_onlyId => <entity><field name="id" type="java.lang.String">{ e.id }</field></entity> % Attribute("className", Text(e.getClass.getName), Null) }}
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
          { ent.map { case e: Entity_onlyId => <entity><field name="id" type="java.util.UUID">{ e.id.toString }</field></entity> % Attribute("className", Text(ent.getClass.getName), Null) }}
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

      "entity node has correct child node 'field@id'" in {
        val (node, ent) = createAndClose_Entity_onlyId()
        (node \ "field")(0).text should be (ent.id)
      }

      "entity node has correct child count" in {
        (createAndClose_Entity_onlyId()._1 \ "field") // get nodes only
          .size should be (1)
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

      "entity node has correct child node 'id': 'field@name'" in {
        ((createAndClose_Entity_moreSimple()._1 \ "field")(0) \ "@name").text should be ("id")
      }
      "entity node has correct child node 'id': 'field@type'" in {
        ((createAndClose_Entity_moreSimple()._1 \ "field")(0) \ "@type").text should be (classOf[UUID].getName)
      }
      "entity node has correct child node 'id': 'field@text'" in {
        val (node,ent) = createAndClose_Entity_moreSimple()
        UUID.fromString((node \ "field")(0).text) should be (ent.id)
      }

      "entity node has correct child node 'name': 'field@name'" in {
        ((createAndClose_Entity_moreSimple()._1 \ "field")(1) \ "@name").text should be ("name")
      }
      "entity node has correct child node 'name': 'field@type'" in {
        ((createAndClose_Entity_moreSimple()._1 \ "field")(1) \ "@type").text should be (classOf[String].getName)
      }
      "entity node has correct child node 'name': 'field@text'" in {
        val (node,ent) = createAndClose_Entity_moreSimple()
        (node \ "field")(1).text should be (ent.name)
      }

      "entity node has correct child node 'age': 'field@name'" in {
        ((createAndClose_Entity_moreSimple()._1 \ "field")(2) \ "@name").text should be ("age")
      }
      "entity node has correct child node 'age': 'field@type'" in {
        ((createAndClose_Entity_moreSimple()._1 \ "field")(2) \ "@type").text should be (classOf[Integer].getName)
      }
      "entity node has correct child node 'age': 'field@text'" in {
        val (node,ent) = createAndClose_Entity_moreSimple()
        Integer.parseInt((node \ "field")(2).text) should be (ent.age)
      }

      "entity node has correct child count" in {
        (createAndClose_Entity_moreSimple()._1 \ "field") // get nodes only
          .size should be (3)
      }

      def createAndClose_Entity_moreSimple(): (Node, Entity_moreSimple) = {
        val ent = Entity_moreSimple()
        val (dao, file) = createDao(ent)
        dao.close()

        ((XML.load(file.read().reader()) \ "entity")(0), ent)
      }
    }
  }

  "deSerialize an entity (Entity_onlyId)" when {
    "doing generic checks" should {
      "have an entity count of 1 after init" in {
        val (file, ent) = create_file()

        val dao = new XmlDao(file)
        dao.read().size should be (1)
      }

      "should be the same" in {
        val (file, ent) = create_file()

        new XmlDao(file).read(ent.id) should be (Some(ent))
      }
    }

    def create_file(): (FileObject, Entity_onlyId) = {
      val ent = Entity_onlyId()
      val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")
      val xml = <root>
        { <entity><field name="id" type="java.util.UUID">{ ent.id.toString }</field></entity> % Attribute("className", Text(ent.getClass.getName), Null) }
      </root>

      file.createFile()
      file.write(new PrettyPrinter(200, 4).format(xml))
      file.close()

      (file, ent)
    }
  }

  "deSerialize an entity (Entity_moreComplex)" when {
    "doing generic checks" should {
      "have an entity count of 1 after init" in {
        val (file, ent) = create_file()

        val dao = new XmlDao(file)
        dao.read().size should be(1)
      }
    }

    "test fields" should {
      "field 'id' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].id should be (ent.id)
      }
      "field 'v01' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v01 should be (ent.v01)
      }
      "field 'v02' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v02 should be (ent.v02)
      }
      "field 'v03' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v03 should be (ent.v03)
      }
      "field 'v04' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v04 should be (ent.v04)
      }
      "field 'v05' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v05 should be (ent.v05)
      }
      "field 'v06' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v06 should be (ent.v06)
      }
      "field 'v07' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v07 should be (ent.v07)
      }
      "field 'v08' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v08 should be (ent.v08)
      }
      "field 'v09' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v09 should be (ent.v09)
      }
      "field 'v10' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v10 should be (ent.v10)
      }
      "field 'v11' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v11 should be (ent.v11)
      }
      "field 'v12' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v12 should be (ent.v12)
      }
    }

    def create_file(): (FileObject, Entity_moreComplex) = {
      val ent = Entity_moreComplex(
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
      val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")
      val xml = <root>
        {
          <entity>
            <field name="id" type="java.util.UUID">{ ent.id.toString }</field>
            <field name="v01" type="java.lang.Byte">{ ent.v01.toString }</field>
            <field name="v02" type="java.lang.Byte">{ ent.v02.toString }</field>
            <field name="v03" type="java.lang.Character">{ ent.v03.toString }</field>
            <field name="v04" type="java.lang.Character">{ ent.v04.toString }</field>
            <field name="v05" type="java.lang.Double">{ ent.v05.toString }</field>
            <field name="v06" type="java.lang.Double">{ ent.v06.toString }</field>
            <field name="v07" type="java.lang.Float">{ ent.v07.toString }</field>
            <field name="v08" type="java.lang.Float">{ ent.v08.toString }</field>
            <field name="v09" type="java.lang.Integer">{ ent.v09.toString }</field>
            <field name="v10" type="java.lang.Integer">{ ent.v10.toString }</field>
            <field name="v11" type="java.lang.String">{ ent.v11.toString }</field>
            <field name="v12" type="java.lang.String">{ ent.v12.toString }</field>
          </entity> % Attribute("className", Text(ent.getClass.getName), Null)
        }
      </root>

      file.createFile()
      file.write(new PrettyPrinter(200, 4).format(xml))
      file.close()

      (file, ent)
    }
  }

  "deSerialize an entity (Entity_moreListString)" when {
    "doing generic checks" should {
      "have an entity count of 1 after init" in {
        val (file, ent) = create_file()

        val dao = new XmlDao(file)
        dao.read().size should be(1)
      }
    }

    "test fields" should {
      "field 'id' should be correct" in {
        val (file, ent) = create_file()
        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].id should be (ent.id)
      }
      "field 'v01' should be correct" in {
        val (file, ent) = create_file()
//        new XmlDao(file).read(ent.id).get.asInstanceOf[Entity_moreComplex].v01 should be (ent.v01)
      }
    }

    def create_file(): (FileObject, Entity_moreListString) = {
      val list = (0 to RandomHelper.rndInt(upper = 100)).map { i => RandomHelper.rndString() }.toList
      val ent = Entity_moreListString(list = list)
      val file = VFS.getManager.resolveFile(s"ram://XmlDaoSpec/${UUID.randomUUID()}.xml")
      val xml = <root>
        {
          <entity>
            <field name="id" type="java.util.UUID">{ ent.id.toString }</field>
            <field name="list" type="List[String]">{ "klöj" }</field>
          </entity> % Attribute("className", Text(ent.getClass.getName), Null)
        }
      </root>

      file.createFile()
      file.write(new PrettyPrinter(200, 4).format(xml))
      file.close()

      (file, ent)
    }
  }

  "serialize an entity (Entity_onlyId)" when {
    "doing generic checks" should {
      "entity node count == 1" in {
        val (dao, file) = createDao(Entity_onlyId())
        dao.close()

        (XML.load(file.read().reader()) \ "entity").size should be(1)
      }

      "entity node has correct attribute 'className'" in {
        (createAndClose_Entity_moreComplex()._1 \ "@className").text should be(classOf[Entity_onlyId].getName)
      }
    }

    "checking field 'id" should {
      val tto = createAndGet(0)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("id")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[UUID].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.id.toString)
      }
    }

    def createAndGet(idx: Int): (Node, Entity_onlyId) = {
      val (node,ent) = createAndClose_Entity_moreComplex()

      ((node \ "field")(idx), ent)
    }

    def createAndClose_Entity_moreComplex(): (Node, Entity_onlyId) = {
      val ent = Entity_onlyId()
      val (dao, file) = createDao(ent)
      dao.close()

      ((XML.load(file.read().reader()) \ "entity")(0), ent)
    }
  }

  "serialize an entity (Entity_moreList)" when {
    "doing generic checks" should {
      "entity node count == 1" in {
        val (dao, file) = createDao(Entity_moreList(list = List.empty))
        dao.close()

        (XML.load(file.read().reader()) \ "entity").size should be(1)
      }

      "entity node has correct attribute 'className'" in {
        (createAndClose_Entity_moreComplex()._1 \ "@className").text should be(classOf[Entity_moreList].getName)
      }
    }

    "checking field 'id" should {
      val tto = createAndGet(0)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("id")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[UUID].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.id.toString)
      }
    }

    "checking field 'list" should {
      val tto = createAndGet(1)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("list")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be ("List[Int]")
      }
      "should have correct item count" in {
        val (node,ent) = tto
        (node \ "_").size should be (ent.list.size)
      }
      "should have correct item nodes" in {
        val (node,ent) = tto

        (node \ "_")      // get all direct 'item' child nodes
          .map(_.text)    // get the serialized values
          .zip(ent.list)  // zip with entity values
          .zipWithIndex   // make an index for easier debugging
          .foreach { i =>
          withClue(s"index ${i._2} of the list is not serialize correctly") {
            i._1._1 should be (i._1._2.toString)
          }
        }
      }
    }

    def createAndGet(idx: Int): (Node, Entity_moreList) = {
      val (node,ent) = createAndClose_Entity_moreComplex()
      ((node \ "field")(idx), ent)
    }

    def createAndClose_Entity_moreComplex(): (Node, Entity_moreList) = {
      val list = (0 to RandomHelper.rndInt(upper = 100)).map { i => RandomHelper.rndInt() }.toList
      val ent = Entity_moreList(list = list)
      val (dao, file) = createDao(ent)
      dao.close()

      ((XML.load(file.read().reader()) \ "entity")(0), ent)
    }
  }

  "serialize an entity with ALL supported data types (Entity_moreComplex)" when {
    "doing generic checks" should {
      "entity node count == 1" in {
        val (dao, file) = createDao(
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
          ))
        dao.close()

        (XML.load(file.read().reader()) \ "entity").size should be(1)
      }

      "entity node has correct attribute 'className'" in {
        (createAndClose_Entity_moreComplex()._1 \ "@className").text should be(classOf[Entity_moreComplex].getName)
      }
    }

    "checking field 'id" should {
      val tto = createAndGet(0)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("id")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[UUID].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.id.toString)
      }
    }

    "checking field 'v01" should {
      val tto = createAndGet(1)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v01")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Byte].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v01.toString)
      }
    }

    "checking field 'v02" should {
      val tto = createAndGet(2)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v02")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Byte].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v02.toString)
      }
    }

    "checking field 'v03" should {
      val tto = createAndGet(3)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v03")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Character].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v03.toString)
      }
    }

    "checking field 'v04" should {
      val tto = createAndGet(4)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v04")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Character].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v04.toString)
      }
    }

    "checking field 'v05" should {
      val tto = createAndGet(5)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v05")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Double].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v05.toString)
      }
    }

    "checking field 'v06" should {
      val tto = createAndGet(6)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v06")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Double].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v06.toString)
      }
    }

    "checking field 'v07" should {
      val tto = createAndGet(7)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v07")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Float].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v07.toString)
      }
    }

    "checking field 'v08" should {
      val tto = createAndGet(8)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v08")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Float].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v08.toString)
      }
    }

    "checking field 'v09" should {
      val tto = createAndGet(9)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v09")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Integer].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v09.toString)
      }
    }

    "checking field 'v10" should {
      val tto = createAndGet(10)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v10")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.Integer].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v10.toString)
      }
    }

    "checking field 'v11" should {
      val tto = createAndGet(11)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v11")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.String].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v11.toString)
      }
    }

    "checking field 'v12" should {
      val tto = createAndGet(12)

      "should have correct attr: 'field@name'" in {
        (tto._1 \ "@name").text should be ("v12")
      }
      "should have correct attr: 'field@type'" in {
        (tto._1 \ "@type").text should be (classOf[java.lang.String].getName)
      }
      "should have correct text: 'field@text'" in {
        val (node,ent) = tto
        node.text should be (ent.v12)
      }
    }

    def createAndGet(idx: Int): (Node, Entity_moreComplex) = {
      val (node,ent) = createAndClose_Entity_moreComplex()

      ((node \ "field")(idx), ent)
    }

    def createAndClose_Entity_moreComplex(): (Node, Entity_moreComplex) = {
      val ent = Entity_moreComplex(
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
      val (dao, file) = createDao(ent)
      dao.close()

      ((XML.load(file.read().reader()) \ "entity")(0), ent)
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
