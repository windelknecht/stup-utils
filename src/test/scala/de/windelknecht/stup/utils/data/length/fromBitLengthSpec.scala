package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class fromBitLengthSpec
  extends WordSpecLike
  with Matchers {
  "want as bit" should {
    "return correct len" in {
      new BitLength(13).toBits should be (13)
    }
  }

  "want as byte" should {
    "return correct len (aligned)" in {
      new BitLength(16).toBytes should be (2d)
    }

    "return correct len (unaligned)" in {
      new BitLength(13).toBytes should be (13d/8)
    }
  }
}
