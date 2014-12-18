package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class fromBitLengthToSISpec
  extends WordSpecLike
  with Matchers {
  "want as kB" should {
    "return correct len (aligned)" in {
      new fromBitLengthToSI(BitLength(8d * 64 * 1000)).asKB should be (64)
    }

    "return correct len (unaligned)" in {
      new fromBitLengthToSI(BitLength(8d * (63 * 1000 + 9))).asKB should be (63.009d)
    }
  }

  "want as MB" should {
    "return correct len (aligned)" in {
      new fromBitLengthToSI(BitLength(8 * 64000000d)).asMB should be (64)
    }

    "return correct len (unaligned)" in {
      new fromBitLengthToSI(BitLength(8 * (63000000d + 9))).asMB should be (63.000009d)
    }
  }

  "want as GB" should {
    "return correct len (aligned)" in {
      new fromBitLengthToSI(BitLength(8 * 64000000000d)).asGB should be (64)
    }

    "return correct len (unaligned)" in {
      new fromBitLengthToSI(BitLength(8 * (63000000000d + 9))).asGB should be (63.000000009000004d)
    }
  }

  "want as TB" should {
    "return correct len (aligned)" in {
      new fromBitLengthToSI(BitLength(8 * 64000000000000d)).asTB should be (64)
    }

    "return correct len (unaligned)" in {
      new fromBitLengthToSI(BitLength(8 * (63000000000000d + 9))).asTB should be (63.000000000009d)
    }
  }

  "want as PB" should {
    "return correct len (aligned)" in {
      new fromBitLengthToSI(BitLength(8 * 64000000000000000d)).asPB should be (64)
    }

    "return correct len (unaligned)" in {
      new fromBitLengthToSI(BitLength(8 * (63000000000000000d + 9))).asPB should be (63.00000000000001d)
    }
  }

  "want as EB" should {
    "return correct len (aligned)" in {
      new fromBitLengthToSI(BitLength(8 * 64000000000000000000d)).asEB should be (64)
    }

    "return correct len (unaligned)" in {
      new fromBitLengthToSI(BitLength(8 * (63000000000000000000d + 9000000d))).asEB should be (63.000000000009d)
    }
  }

  "want as ZB" should {
    "return correct len (aligned)" in {
      new fromBitLengthToSI(BitLength(8 * 64000000000000000000000d)).asZB should be (64)
    }

    "return correct len (unaligned)" in {
      new fromBitLengthToSI(BitLength(8 * (63000000000000000000000d + 900000000d))).asZB should be (63.00000000000091d)
    }
  }

  "want as YB" should {
    "return correct len (aligned)" in {
      new fromBitLengthToSI(BitLength(8 * 64000000000000000000000000d)).asYB should be (64)
    }

    "return correct len (unaligned)" in {
      new fromBitLengthToSI(BitLength(8 * (63000000000000000000000000d + 90000000000000000d))).asYB should be (63.00000009d)
    }
  }
}
