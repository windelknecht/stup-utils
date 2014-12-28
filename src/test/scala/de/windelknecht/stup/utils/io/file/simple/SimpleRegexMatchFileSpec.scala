package de.windelknecht.stup.utils.io.file.simple

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class SimpleRegexMatchFileSpec
  extends WordSpec
  with Matchers
  with MockitoSugar {
  private val _goodFile = """produkte="Fritz_Box_HW185"
    |anzeige_produkte="FRITZ.Box_7490"
    |oem_liste="avm 1und1 "
    |flash_size="0-sflash_size=1MB-nand_size=512MBMB"
    |land_liste="049"
    |params_liste=""
    |sprachen="de"
    |make_j_option="4"
    |serial_number="0000000000000000"
    |# Warning: Check ~/.MenueSystem/.firmwaremenu (if exist)
    |firmware_geometry_x=120
    |firmware_geometry_y=50
    |firmware_background=lightyellow
    |enable_entwicklungsversion=0
    |firmware_use_user_defined_color_file_local=
    |firmware_use_user_defined_color_file_xterm=
    |firmware_xterm_position=0+0""".stripMargin
  private val _goodFileWritten = """produkte="Fritz_Box_HW185"
    |anzeige_produkte="FRITZ.Box_7490"
    |oem_liste="avm 1und1 "
    |flash_size="0-sflash_size=1MB-nand_size=512MBMB"
    |land_liste="049"
    |params_liste=""
    |sprachen="de"
    |make_j_option="4"
    |serial_number="0000000000000000"
    |# Warning: Check ~/.MenueSystem/.firmwaremenu (if exist)
    |firmware_geometry_x=120
    |firmware_geometry_y=50
    |firmware_background=lightyellow
    |enable_entwicklungsversion=0
    |firmware_use_user_defined_color_file_local=
    |firmware_use_user_defined_color_file_xterm=
    |firmware_xterm_position=heiko""".stripMargin

  "simple regex match file instance (correct defined regex)" when {
    "reading" should {
      "find 1st line regex" in {
        new SimpleRegexMatchFile(createMock(), pattern = List("""^(produkte)=\"(.*)\"""".r)).read().get("produkte") should be ("Fritz_Box_HW185")
      }
      "find regex in the middle" in {
        new SimpleRegexMatchFile(createMock(), pattern = List("""^(oem_liste)=\"(.*)\"""".r)).read().get("oem_liste") should be ("avm 1und1")
      }
      "find last line regex" in {
        new SimpleRegexMatchFile(createMock(), pattern = List("""^(firmware_xterm_position)=(.*)""".r)).read().get("firmware_xterm_position") should be ("0+0")
      }

      "return isEmpty==true when no input" in {
        new SimpleRegexMatchFile(createMock(""), pattern = List("""^(oem_liste)=\"(.*)\"""".r)).read().isEmpty should be (right = true)
      }
    }

    "writing" should {
      "be correct" in {
        val m = createMock()

        new SimpleRegexMatchFile(m, pattern = List("""^(firmware_xterm_position)=(.*)""".r)).read().set("firmware_xterm_position", "heiko")

        verify(m).write(_goodFileWritten, force = false)
      }
    }
  }

  "simple regex match file instance (invalid regex)" when {
    "reading" should {
      "throw an exception" in {
        an[IllegalArgumentException] should be thrownBy { new SimpleRegexMatchFile(createMock(), pattern = List("""^produkte=\"(.*)\"""".r)).read().get("produkte") should be ("Fritz_Box_HW185") }
      }
    }
  }

  private def createMock(content: String = _goodFile): DataProvider = {
    val m = mock[DataProvider]

    when(m.read()).thenReturn(content.split("\n").toList)

    m
  }
}
