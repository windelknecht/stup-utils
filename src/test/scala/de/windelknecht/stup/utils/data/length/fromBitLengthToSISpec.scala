package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class fromBitLengthToSISpec
  extends WordSpecLike
  with Matchers {
  "want as kB" should {
    "return correct len (aligned)" in {
      new BitLength(8d * 64 * 1000).toKB should be (64)
    }

    "return correct len (unaligned)" in {
      new BitLength(8d * (63 * 1000 + 9)).toKB should be (63.009d)
    }
  }

  "want as MB" should {
    "return correct len (aligned)" in {
      new BitLength(8 * 64000000d).toMB should be (64)
    }

    "return correct len (unaligned)" in {
      new BitLength(8 * (63000000d + 9)).toMB should be (63.000009d)
    }
  }

  "want as GB" should {
    "return correct len (aligned)" in {
      new BitLength(8 * 64000000000d).toGB should be (64)
    }

    "return correct len (unaligned)" in {
      new BitLength(8 * (63000000000d + 9)).toGB should be (63.000000009000004d)
    }
  }

  "want as TB" should {
    "return correct len (aligned)" in {
      new BitLength(8 * 64000000000000d).toTB should be (64)
    }

    "return correct len (unaligned)" in {
      new BitLength(8 * (63000000000000d + 9)).toTB should be (63.000000000009d)
    }
  }

  "want as PB" should {
    "return correct len (aligned)" in {
      new BitLength(8 * 64000000000000000d).toPB should be (64)
    }

    "return correct len (unaligned)" in {
      new BitLength(8 * (63000000000000000d + 9)).toPB should be (63.00000000000001d)
    }
  }

  "want as EB" should {
    "return correct len (aligned)" in {
      new BitLength(8 * 64000000000000000000d).toEB should be (64)
    }

    "return correct len (unaligned)" in {
      new BitLength(8 * (63000000000000000000d + 9000000d)).toEB should be (63.000000000009d)
    }
  }

  "want as ZB" should {
    "return correct len (aligned)" in {
      new BitLength(8 * 64000000000000000000000d).toZB should be (64)
    }

    "return correct len (unaligned)" in {
      new BitLength(8 * (63000000000000000000000d + 900000000d)).toZB should be (63.00000000000091d)
    }
  }

  "want as YB" should {
    "return correct len (aligned)" in {
      new BitLength(8 * 64000000000000000000000000d).toYB should be (64)
    }

    "return correct len (unaligned)" in {
      new BitLength(8 * (63000000000000000000000000d + 90000000000000000d)).toYB should be (63.00000009d)
    }
  }
}
