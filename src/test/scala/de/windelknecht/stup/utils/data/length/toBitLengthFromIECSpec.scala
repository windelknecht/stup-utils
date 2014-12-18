package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class toBitLengthFromIECSpec
  extends WordSpecLike
  with Matchers {
  "want a kB" when {
    "using function Kibibyte" should { test("Kibibyte", toBitLengthFromIEC(8).asKibibyte,  65536d) }
    "using function KiB"      should { test("KiB",      toBitLengthFromIEC(1).asKiB,         8192d) }
  }

  "want a MB" when {
    "using function Mebibyte" should { test("Mebibyte", toBitLengthFromIEC(8).asMebibyte,  67108864d) }
    "using function MiB"      should { test("MiB",      toBitLengthFromIEC(1).asMiB,         8388608d) }
  }

  "want a GB" when {
    "using function Gibibyte" should { test("Gibibyte", toBitLengthFromIEC(8).asGibibyte, 68719476736d) }
    "using function GiB"      should { test("GiB",      toBitLengthFromIEC(1).asGiB,       8589934592d) }
  }

  "want a TB" when {
    "using function Tebibyte" should { test("Tebibyte", toBitLengthFromIEC(8).asTebibyte, 70368744177664d) }
    "using function TiB"      should { test("TiB",      toBitLengthFromIEC(1).asTiB,       8796093022208d) }
  }

  "want a PB" when {
    "using function Pebibyte" should { test("Pebibyte", toBitLengthFromIEC(8).asPebibyte, 72057594037927936d) }
    "using function PiB"      should { test("PiB",      toBitLengthFromIEC(1).asPiB,       9007199254740992d) }
  }

  "want a EB" when {
    "using function Exbibyte" should { test("Exbibyte", toBitLengthFromIEC(8).asExbibyte, 64d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024) }
    "using function EiB"      should { test("EiB",      toBitLengthFromIEC(1).asEiB,       8d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024) }
  }

  "want a ZB" when {
    "using function Zebibyte" should { test("Zebibyte", toBitLengthFromIEC(8).asZebibyte, 64d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024) }
    "using function ZiB"      should { test("ZiB",      toBitLengthFromIEC(1).asZiB,       8d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024) }
  }

  "want a YB" when {
    "using function Yobibyte" should { test("Yobibyte", toBitLengthFromIEC(8).asYobibyte, 64d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024) }
    "using function YiB"      should { test("YiB",      toBitLengthFromIEC(1).asYiB,       8d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024) }
  }

  def test(
    func: String,
    data: BitLength,
    expectedLen: Double
    ): Unit = {
    s"return correct len" in {
      withClue(s"function '$func': $data.len should be $expectedLen, but") {
        data.len should be(expectedLen)
      }
    }
  }
}
