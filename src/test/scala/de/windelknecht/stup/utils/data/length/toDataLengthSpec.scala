package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class toDataLengthSpec
  extends WordSpecLike
  with Matchers {
  "" when {
    "using function 'bit" should {
      "return correct computed len" in {
        new toDataLength(8).bit.len should be (8)
      }

      "return correct unit" in {
        new toDataLength(8).bit.unit should be (DataLengthUnit.bit)
      }
    }

    "using function 'bits" should {
      "return correct computed len" in {
        new toDataLength(8).bits.len should be (8)
      }

      "return correct unit" in {
        new toDataLength(8).bits.unit should be (DataLengthUnit.bit)
      }
    }

    "using function 'byte" should {
      "return correct computed len" in {
        new toDataLength(8).byte.len should be (8)
      }

      "return correct unit" in {
        new toDataLength(8).byte.unit should be (DataLengthUnit.byte)
      }
    }

    "using function 'bytes" should {
      "return correct computed len" in {
        new toDataLength(8).byte.len should be (8)
      }

      "return correct unit" in {
        new toDataLength(8).byte.unit should be (DataLengthUnit.byte)
      }
    }
  }
}
