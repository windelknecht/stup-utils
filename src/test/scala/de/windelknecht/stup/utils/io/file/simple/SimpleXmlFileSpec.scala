package de.windelknecht.stup.utils.io.file.simple

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import org.xml.sax.SAXParseException

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 05.06.14
 * Time: 15:30
 *
 */
class SimpleXmlFileSpec
  extends WordSpec
  with Matchers
  with MockitoSugar {
  private val _xml = """<root>
      |  <property name="activeCountries">049</property>
      |  <property name="activeLanguages">de</property>
      |  <property name="activeOems">avm,1und1</property>
      |  <property name="compilerMakeJ">1</property>
      |  <property name="make::gu::CALL_GUPL_ONLY">0</property>
      |  <property name="make::gu::CHECKOUT_ALL">43</property>
      |</root>""".stripMargin
  private val _customXml = """<root>
      |  <node attr="activeCountries">049</node>
      |  <node attr="activeLanguages">de</node>
      |</root>""".stripMargin
  private val _invalidXml = """<root>
      |  <node attr="activeCountries">049</sohi>
      |  <node attr="activeLanguages">de</node>
      |</root>""".stripMargin

  "simple xml file instance (with default node and attribute names)" when {
    "reading" should {
      "return prop node 1" in {
        new SimpleXmlFile(createMock()).read().get("activeCountries") should be ("049")
      }
      "return prop node 2" in {
        new SimpleXmlFile(createMock()).read().get("activeLanguages") should be ("de")
      }
      "return prop node 3" in {
        new SimpleXmlFile(createMock()).read().get("activeOems") should be ("avm,1und1")
      }
      "return prop node 4" in {
        new SimpleXmlFile(createMock()).read().get("compilerMakeJ") should be ("1")
      }
      "return prop node 5" in {
        new SimpleXmlFile(createMock()).read().get("make::gu::CALL_GUPL_ONLY") should be ("0")
      }
      "return prop node 6" in {
        new SimpleXmlFile(createMock()).read().get("make::gu::CHECKOUT_ALL") should be ("43")
      }

      "return isEmpty==true when no input" in {
        new SimpleXmlFile(createMock("")).read().isEmpty should be (right = true)
      }
    }

    "writing" should {
      "be correct" in {
        val m = createMock()

        new SimpleXmlFile(m).read().set("compilerMakeJ", "1")

        verify(m).write(_xml, force = false)
      }
    }
  }

  "simple xml file instance (with custom node and attribute names)" when {
    "reading" should {
      "return prop node 1" in {
        new SimpleXmlFile(createMock(_customXml), nodeName = "node", attrName = "attr").read().get("activeCountries") should be ("049")
      }
      "return prop node 2" in {
        new SimpleXmlFile(createMock(_customXml), nodeName = "node", attrName = "attr").read().get("activeLanguages") should be ("de")
      }
    }

    "writing" should {
      "return prop node 1" in {
        val m = createMock(_customXml)

        new SimpleXmlFile(m, nodeName = "node", attrName = "attr").read().set("activeLanguages", "de")

        verify(m).write(_customXml, force = false)
      }
    }
  }

  "simple xml file instance (with invalid xml)" when {
    "reading" should {
      "throw an exception" in {
        an[SAXParseException] should be thrownBy new SimpleXmlFile(createMock(_invalidXml)).read()
      }
    }
  }

  private def createMock(content: String = _xml): DataProvider = {
    val m = mock[DataProvider]

    when(m.read()).thenReturn(content.split("\n").toList)

    m
  }
}
