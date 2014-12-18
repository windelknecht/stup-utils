package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class toBitLengthSpec
  extends WordSpecLike
  with Matchers {
  "want a bit" when {
    "using function bit"   should { test("bit",   toBitLength(8).bit, 8) }
    "using function bits"  should { test("bits",  toBitLength(8).bits, 8) }
    "using function Bit"   should { test("Bit",   toBitLength(8).Bit, 8) }
    "using function Bits"  should { test("Bits",  toBitLength(8).Bits, 8) }
  }

  "want a byte" when {
    "using function byte"  should { test("byte",  toBitLength(8).byte, 64) }
    "using function bytes" should { test("bytes", toBitLength(8).bytes, 64) }
    "using function B"     should { test("B",     toBitLength(1).B, 8) }
    "using function Byte"  should { test("Byte",  toBitLength(2).Byte, 16) }
    "using function Bytes" should { test("Bytes", toBitLength(8).Byte, 64) }
  }

  def test(
    func: String,
    data: BitLength,
    expectedLen: Int
    ): Unit = {
    s"return correct len" in {
      withClue(s"function '$func': $data.len should be $expectedLen, but") {
        data.len should be(expectedLen)
      }
    }
  }
}
