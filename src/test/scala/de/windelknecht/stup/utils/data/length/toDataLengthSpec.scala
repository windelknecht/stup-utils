package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class toDataLengthSpec
  extends WordSpecLike
  with Matchers {
  "" when {
    "" should {
      "return correct " in {
        new toDataLength(8).bits.bits should be (8)
        new toDataLength(8).bits.unit should be (DataLengthUnit.bit)
      }
    }
  }
}
