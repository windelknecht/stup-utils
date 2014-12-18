package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class toBitLengthFromSISpec
  extends WordSpecLike
  with Matchers {
  "want a kB" when {
    "using function Kilobyte"  should { test("Kilobyte",  toBitLengthFromSI(8).asKilobyte,  64000d) }
    "using function kB"        should { test("kB",        toBitLengthFromSI(1).asKB,         8000d) }
  }

  "want a MB" when {
    "using function Megabyte"  should { test("Megabyte",  toBitLengthFromSI(8).asMegabyte,  64000000d) }
    "using function MB"        should { test("MB",        toBitLengthFromSI(1).asMB,         8000000d) }
  }

  "want a GB" when {
    "using function Gigabyte"  should { test("Gigabyte",  toBitLengthFromSI(8).asGigabyte,  64000000000d) }
    "using function GB"        should { test("GB",        toBitLengthFromSI(1).asGB,         8000000000d) }
  }

  "want a TB" when {
    "using function Terabyte"  should { test("Terabyte",  toBitLengthFromSI(8).asTerabyte,  64000000000000d) }
    "using function TB"        should { test("TB",        toBitLengthFromSI(1).asTB,         8000000000000d) }
  }

  "want a PB" when {
    "using function Petabyte"  should { test("Petabyte",  toBitLengthFromSI(8).asPetabyte,  64000000000000000d) }
    "using function PB"        should { test("PB",        toBitLengthFromSI(1).asPB,         8000000000000000d) }
  }

  "want a EB" when {
    "using function Exabyte"   should { test("Exabyte",   toBitLengthFromSI(8).asExabyte,   64000000000000000000d) }
    "using function EB"        should { test("EB",        toBitLengthFromSI(1).asEB,         8000000000000000000d) }
  }

  "want a ZB" when {
    "using function Zettabyte" should { test("Zettabyte", toBitLengthFromSI(8).asZettabyte, 64000000000000000000000d) }
    "using function ZB"        should { test("ZB",        toBitLengthFromSI(1).asZB,         8000000000000000000000d) }
  }

  "want a YB" when {
    "using function Yottabyte" should { test("Yottabyte", toBitLengthFromSI(8).asYottabyte, 64000000000000000000000000d) }
    "using function YB"        should { test("YB",        toBitLengthFromSI(1).asYB,         8000000000000000000000000d) }
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
