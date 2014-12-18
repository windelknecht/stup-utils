package de.windelknecht.stup.utils.data.length

import de.windelknecht.stup.utils.coding.Implicits._
import de.windelknecht.stup.utils.data.length._
import org.scalatest.{Matchers, WordSpecLike}

class ByteLengthSpec
  extends WordSpecLike
  with Matchers {
  "" when {
    "" should {
      "sss" in {
        val so = (1 asYiB) + (50 asMiB)

        ByteLength(so)

        println("")
      }
    }
  }
}
