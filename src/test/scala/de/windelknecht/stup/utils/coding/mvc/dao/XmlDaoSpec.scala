package de.windelknecht.stup.utils.coding.mvc.dao

import de.windelknecht.stup.utils.io.vfs._
import org.apache.commons.vfs2.VFS
import org.scalatest.{Matchers, WordSpec}

import scala.xml.PrettyPrinter

class XmlDaoSpec
  extends WordSpec
  with Matchers {
  val file1 = VFS.getManager.resolveFile("ram://XmlDaoSpec/file1.xml")
  val xml = <root>
    <entity className=""></entity>
  </root>

  file1.write(new PrettyPrinter(80, 4).format(xml))
  file1.close()

  "" when {
    "" should {
      "" in {
        new XmlDao(file1)
      }
    }
  }
}
