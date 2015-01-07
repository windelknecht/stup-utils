package de.windelknecht.stup.utils.coding.mvc

import java.util.UUID

import de.windelknecht.stup.utils.tools.RandomHelper
import org.scalatest.{Matchers, WordSpec}

class mock_simpleClass(
  val id: String
  ) extends Entity

case class mock_simpleCaseClass_1Arg(
  id: String
  ) extends Entity

case class mock_simpleCaseClass_1Arg1Def(
  id: String = UUID.randomUUID().toString
  ) extends Entity

case class mock_simpleCaseClass_2Arg(
  id: String,
  v1: Int
  ) extends Entity

case class mock_simpleCaseClass_2Arg1Def(
  id: String = UUID.randomUUID().toString,
  v1: Int
  ) extends Entity

case class mock_simpleCaseClass_Char(id: String, v1: Char) extends Entity
case class mock_simpleCaseClass_CharJ(id: String, v1: java.lang.Character) extends Entity
case class mock_simpleCaseClass_CharS(id: String, v1: scala.Char) extends Entity

case class mock_simpleCaseClass_Bool(id: String, v1: Boolean) extends Entity
case class mock_simpleCaseClass_BoolJ(id: String, v1: java.lang.Boolean) extends Entity
case class mock_simpleCaseClass_BoolS(id: String, v1: scala.Boolean) extends Entity

case class mock_simpleCaseClass_Byte(id: String, v1: Byte) extends Entity
case class mock_simpleCaseClass_ByteJ(id: String, v1: java.lang.Byte) extends Entity
case class mock_simpleCaseClass_ByteS(id: String, v1: scala.Byte) extends Entity

case class mock_simpleCaseClass_Double(id: String, v1: Double) extends Entity
case class mock_simpleCaseClass_DoubleJ(id: String, v1: java.lang.Double) extends Entity
case class mock_simpleCaseClass_DoubleS(id: String, v1: scala.Double) extends Entity

case class mock_simpleCaseClass_Float(id: String, v1: Float) extends Entity
case class mock_simpleCaseClass_FloatJ(id: String, v1: java.lang.Float) extends Entity
case class mock_simpleCaseClass_FloatS(id: String, v1: scala.Float) extends Entity

case class mock_simpleCaseClass_Int(id: String, v1: Int) extends Entity
case class mock_simpleCaseClass_IntJ(id: String, v1: java.lang.Integer) extends Entity
case class mock_simpleCaseClass_IntS(id: String, v1: scala.Int) extends Entity

case class mock_simpleCaseClass_Long(id: String, v1: Long) extends Entity
case class mock_simpleCaseClass_LongJ(id: String, v1: java.lang.Long) extends Entity
case class mock_simpleCaseClass_LongS(id: String, v1: scala.Long) extends Entity

case class mock_simpleCaseClass_Short(id: String, v1: Short) extends Entity
case class mock_simpleCaseClass_ShortJ(id: String, v1: java.lang.Short) extends Entity
case class mock_simpleCaseClass_ShortS(id: String, v1: scala.Short) extends Entity

case class mock_simpleCaseClass_String(id: String, v1: String) extends Entity
case class mock_simpleCaseClass_StringJ(id: String, v1: java.lang.String) extends Entity

case class mock_simpleCaseClass_Option(id: String, v1: Option[String]) extends Entity

case class mock_simpleCaseClass_notFromEntity(
  id: String = UUID.randomUUID().toString,
  v1: Int
  )

class EntitySpec
  extends WordSpec
  with Matchers {
  "using method 'create(String)'" when {
    "testing w/o default args" should {
      "return a case class w/ 1/0 arg(s)" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_1Arg").isInstanceOf[mock_simpleCaseClass_1Arg] should be(right = true)
      }

      "return a case class w/ 2/0 arg(s)" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_2Arg").isInstanceOf[mock_simpleCaseClass_2Arg] should be(right = true)
      }
    }

    "testing w/ default args" should {
      "return a case class w/ 1/1 arg(s)" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_1Arg1Def").isInstanceOf[mock_simpleCaseClass_1Arg1Def] should be(right = true)
      }

      "return a case class w/ 2/1 arg(s)" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_2Arg1Def").isInstanceOf[mock_simpleCaseClass_2Arg1Def] should be(right = true)
      }
    }

    "class contains a Char" should {
      "return a case class w/ Char" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Char").isInstanceOf[mock_simpleCaseClass_Char] should be(right = true)
      }

      "return a case class w/ CharJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_CharJ").isInstanceOf[mock_simpleCaseClass_CharJ] should be(right = true)
      }

      "return a case class w/ CharS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_CharS").isInstanceOf[mock_simpleCaseClass_CharS] should be(right = true)
      }

      "instantiate correct case class w/ Char" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Char").asInstanceOf[mock_simpleCaseClass_Char].v1 should be(0)
      }

      "instantiate correct case class w/ CharJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_CharJ").asInstanceOf[mock_simpleCaseClass_CharJ].v1 should be(0)
      }

      "instantiate correct case class w/ CharS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_CharS").asInstanceOf[mock_simpleCaseClass_CharS].v1 should be(0)
      }
    }

    "class contains a Boolean" should {
      "return a case class w/ Boolean" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Bool").isInstanceOf[mock_simpleCaseClass_Bool] should be (right = true)
      }

      "return a case class w/ BooleanJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_BoolJ").isInstanceOf[mock_simpleCaseClass_BoolJ] should be (right = true)
      }

      "return a case class w/ BooleanS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_BoolS").isInstanceOf[mock_simpleCaseClass_BoolS] should be (right = true)
      }

      "instantiate correct case class w/ Boolean" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Bool").asInstanceOf[mock_simpleCaseClass_Bool].v1 should be (right = false)
      }

      "instantiate correct case class w/ BooleanJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_BoolJ").asInstanceOf[mock_simpleCaseClass_BoolJ].v1.booleanValue() should be (right = false)
      }

      "instantiate correct case class w/ BooleanS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_BoolS").asInstanceOf[mock_simpleCaseClass_BoolS].v1 should be (right = false)
      }
    }

    "class contains a Byte" should {
      "return a case class w/ Byte" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Byte").isInstanceOf[mock_simpleCaseClass_Byte] should be(right = true)
      }

      "return a case class w/ ByteJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_ByteJ").isInstanceOf[mock_simpleCaseClass_ByteJ] should be(right = true)
      }

      "return a case class w/ ByteS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_ByteS").isInstanceOf[mock_simpleCaseClass_ByteS] should be(right = true)
      }

      "instantiate correct case class w/ Byte" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Byte").asInstanceOf[mock_simpleCaseClass_Byte].v1 should be(0)
      }

      "instantiate correct case class w/ ByteJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_ByteJ").asInstanceOf[mock_simpleCaseClass_ByteJ].v1 should be(0)
      }

      "instantiate correct case class w/ ByteS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_ByteS").asInstanceOf[mock_simpleCaseClass_ByteS].v1 should be(0)
      }
    }

    "class contains a Double" should {
      "return a case class w/ Double" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Double").isInstanceOf[mock_simpleCaseClass_Double] should be(right = true)
      }

      "return a case class w/ DoubleJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_DoubleJ").isInstanceOf[mock_simpleCaseClass_DoubleJ] should be(right = true)
      }

      "return a case class w/ DoubleS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_DoubleS").isInstanceOf[mock_simpleCaseClass_DoubleS] should be(right = true)
      }

      "instantiate correct case class w/ Double" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Double").asInstanceOf[mock_simpleCaseClass_Double].v1 should be(0)
      }

      "instantiate correct case class w/ DoubleJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_DoubleJ").asInstanceOf[mock_simpleCaseClass_DoubleJ].v1 should be(0)
      }

      "instantiate correct case class w/ DoubleS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_DoubleS").asInstanceOf[mock_simpleCaseClass_DoubleS].v1 should be(0)
      }
    }

    "class contains a Float" should {
      "return a case class w/ Float" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Float").isInstanceOf[mock_simpleCaseClass_Float] should be(right = true)
      }

      "return a case class w/ FloatJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_FloatJ").isInstanceOf[mock_simpleCaseClass_FloatJ] should be(right = true)
      }

      "return a case class w/ FloatS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_FloatS").isInstanceOf[mock_simpleCaseClass_FloatS] should be(right = true)
      }

      "instantiate correct case class w/ Float" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Float").asInstanceOf[mock_simpleCaseClass_Float].v1 should be(0)
      }

      "instantiate correct case class w/ FloatJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_FloatJ").asInstanceOf[mock_simpleCaseClass_FloatJ].v1 should be(0)
      }

      "instantiate correct case class w/ FloatS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_FloatS").asInstanceOf[mock_simpleCaseClass_FloatS].v1 should be(0)
      }
    }

    "class contains a Int" should {
      "return a case class w/ Int" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Int").isInstanceOf[mock_simpleCaseClass_Int] should be(right = true)
      }

      "return a case class w/ IntJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_IntJ").isInstanceOf[mock_simpleCaseClass_IntJ] should be(right = true)
      }

      "return a case class w/ IntS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_IntS").isInstanceOf[mock_simpleCaseClass_IntS] should be(right = true)
      }

      "instantiate correct case class w/ Int" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Int").asInstanceOf[mock_simpleCaseClass_Int].v1 should be(0)
      }

      "instantiate correct case class w/ IntJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_IntJ").asInstanceOf[mock_simpleCaseClass_IntJ].v1 should be(0)
      }

      "instantiate correct case class w/ IntS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_IntS").asInstanceOf[mock_simpleCaseClass_IntS].v1 should be(0)
      }
    }

    "class contains a Long" should {
      "return a case class w/ Long" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Long").isInstanceOf[mock_simpleCaseClass_Long] should be(right = true)
      }

      "return a case class w/ LongJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_LongJ").isInstanceOf[mock_simpleCaseClass_LongJ] should be(right = true)
      }

      "return a case class w/ LongS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_LongS").isInstanceOf[mock_simpleCaseClass_LongS] should be(right = true)
      }

      "instantiate correct case class w/ Long" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Long").asInstanceOf[mock_simpleCaseClass_Long].v1 should be(0)
      }

      "instantiate correct case class w/ LongJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_LongJ").asInstanceOf[mock_simpleCaseClass_LongJ].v1 should be(0)
      }

      "instantiate correct case class w/ LongS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_LongS").asInstanceOf[mock_simpleCaseClass_LongS].v1 should be(0)
      }
    }

    "class contains a Short" should {
      "return a case class w/ Short" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Short").isInstanceOf[mock_simpleCaseClass_Short] should be(right = true)
      }

      "return a case class w/ ShortJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_ShortJ").isInstanceOf[mock_simpleCaseClass_ShortJ] should be(right = true)
      }

      "return a case class w/ ShortS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_ShortS").isInstanceOf[mock_simpleCaseClass_ShortS] should be(right = true)
      }

      "instantiate correct case class w/ Short" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Short").asInstanceOf[mock_simpleCaseClass_Short].v1 should be(0)
      }

      "instantiate correct case class w/ ShortJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_ShortJ").asInstanceOf[mock_simpleCaseClass_ShortJ].v1 should be(0)
      }

      "instantiate correct case class w/ ShortS" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_ShortS").asInstanceOf[mock_simpleCaseClass_ShortS].v1 should be(0)
      }
    }

    "class contains a String" should {
      "return a case class w/ String" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_String").isInstanceOf[mock_simpleCaseClass_String] should be(right = true)
      }

      "return a case class w/ StringJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_StringJ").isInstanceOf[mock_simpleCaseClass_StringJ] should be(right = true)
      }

      "instantiate correct case class w/ String" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_String").asInstanceOf[mock_simpleCaseClass_String].v1 should be("")
      }

      "instantiate correct case class w/ StringJ" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_StringJ").asInstanceOf[mock_simpleCaseClass_StringJ].v1 should be("")
      }
    }

    "class contains a Option" should {
      "return a case class w/ Option" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Option").isInstanceOf[mock_simpleCaseClass_Option] should be(right = true)
      }

      "instantiate correct" in {
        Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_Option").asInstanceOf[mock_simpleCaseClass_Option].v1 should be(None)
      }
    }

    "doing failure tests" should {
      "throw an exception if its a class and not a case class" in {
        an[IllegalArgumentException] should be thrownBy { Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleClass") }
      }

      "throw an exception if the class is not derived from Entity" in {
        an[IllegalArgumentException] should be thrownBy { Entity.create("de.windelknecht.stup.utils.coding.mvc.mock_simpleCaseClass_notFromEntity") }
      }

      "throw an exception if the class does not exist" in {
        an[ClassNotFoundException] should be thrownBy { Entity.create("de.windelknecht.stup.utils.coding.mvc.doesNotExist") }
      }
    }
  }

  "using method 'create(String, ...) with provided arguments (Entity with 1 field)" when {
    "given args are correct" should {
      "the 1 and only argument should be injected" in {
        val id = UUID.randomUUID().toString

        Entity.create(classOf[mock_simpleCaseClass_1Arg], id).id should be (id)
      }
    }

    "given args are invalid" should {
      "an IllegalArgumentException should be thrown" in {
        an[IllegalArgumentException] should be thrownBy { Entity.create(classOf[mock_simpleCaseClass_1Arg], 13) }

      }
    }
  }

  "using method 'create(String, ...) with provided arguments (Entity with 2 fields)" when {
    "given args are correct" should {
      "1st argument should be injected" in {
        val id = UUID.randomUUID().toString
        Entity.create(classOf[mock_simpleCaseClass_2Arg], id, RandomHelper.rndInt()).id should be (id)
      }

      "2nd argument should be injected" in {
        val int = RandomHelper.rndInt()
        Entity.create(classOf[mock_simpleCaseClass_2Arg], UUID.randomUUID().toString, int).asInstanceOf[mock_simpleCaseClass_2Arg].v1 should be (int)
      }
    }

    "only 1 arg is provided" should {
      "1st argument should be injected" in {
        val id = UUID.randomUUID().toString
        Entity.create(classOf[mock_simpleCaseClass_2Arg], id).id should be (id)
      }

      "2nd argument has default value" in {
        Entity.create(classOf[mock_simpleCaseClass_2Arg], UUID.randomUUID().toString).asInstanceOf[mock_simpleCaseClass_2Arg].v1 should be (0)
      }
    }
  }
}
