package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class ByteLengthSpec
  extends WordSpecLike
  with Matchers {
  "testing the compagnion object" when {
    "using function getBestMatchingIECUnit" should {
      "return correct B" in { ByteLength.getBestMatchingIECUnit(toBitLength(1).asByte) should be (ByteUnit.B) }
      "return correct KiB" in { ByteLength.getBestMatchingIECUnit(toBitLengthFromIEC(1).asKiB) should be (ByteUnit.KiB) }
      "return correct MiB" in { ByteLength.getBestMatchingIECUnit(toBitLengthFromIEC(1).asMiB) should be (ByteUnit.MiB) }
      "return correct GiB" in { ByteLength.getBestMatchingIECUnit(toBitLengthFromIEC(1).asGiB) should be (ByteUnit.GiB) }
      "return correct TiB" in { ByteLength.getBestMatchingIECUnit(toBitLengthFromIEC(1).asTiB) should be (ByteUnit.TiB) }
      "return correct PiB" in { ByteLength.getBestMatchingIECUnit(toBitLengthFromIEC(1).asPiB) should be (ByteUnit.PiB) }
      "return correct EiB" in { ByteLength.getBestMatchingIECUnit(toBitLengthFromIEC(1).asEiB) should be (ByteUnit.EiB) }
      "return correct ZiB" in { ByteLength.getBestMatchingIECUnit(toBitLengthFromIEC(1).asZiB) should be (ByteUnit.ZiB) }
      "return correct YiB" in { ByteLength.getBestMatchingIECUnit(toBitLengthFromIEC(1).asYiB) should be (ByteUnit.YiB) }
    }

    "using function getBestMatchingSIUnit" should {
      "return correct B" in { ByteLength.getBestMatchingSIUnit(toBitLength(1).asByte) should be (ByteUnit.B) }
      "return correct KB" in { ByteLength.getBestMatchingSIUnit(toBitLengthFromSI(1).asKB) should be (ByteUnit.KB) }
      "return correct MB" in { ByteLength.getBestMatchingSIUnit(toBitLengthFromSI(1).asMB) should be (ByteUnit.MB) }
      "return correct GB" in { ByteLength.getBestMatchingSIUnit(toBitLengthFromSI(1).asGB) should be (ByteUnit.GB) }
      "return correct TB" in { ByteLength.getBestMatchingSIUnit(toBitLengthFromSI(1).asTB) should be (ByteUnit.TB) }
      "return correct PB" in { ByteLength.getBestMatchingSIUnit(toBitLengthFromSI(1).asPB) should be (ByteUnit.PB) }
      "return correct EB" in { ByteLength.getBestMatchingSIUnit(toBitLengthFromSI(1).asEB) should be (ByteUnit.EB) }
      "return correct ZB" in { ByteLength.getBestMatchingSIUnit(toBitLengthFromSI(1).asZB) should be (ByteUnit.ZB) }
      "return correct YB" in { ByteLength.getBestMatchingSIUnit(toBitLengthFromSI(1).asYB) should be (ByteUnit.YB) }
    }
  }

  val r1 = 2 Byte
}
