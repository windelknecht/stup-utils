package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class fromBitLengthSpec
  extends WordSpecLike
  with Matchers {
  "want as bit" should {
    "return correct len" in {
      new fromBitLength(BitLength(13)).asBits should be (13)
    }
  }

  "want as byte" should {
    "return correct len (aligned)" in {
      new fromBitLength(BitLength(16)).asBytes should be (2d)
    }

    "return correct len (unaligned)" in {
      new fromBitLength(BitLength(13)).asBytes should be (13d/8)
    }
  }
}
