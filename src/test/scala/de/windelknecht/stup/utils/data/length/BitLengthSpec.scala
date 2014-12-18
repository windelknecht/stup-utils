package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class BitLengthSpec
  extends WordSpecLike
  with Matchers {
  "testing math methods" when {
    "using function '+'" should {
      "simply add +1 and +1" in {
        (BitLength(13) + BitLength(7)).len should be (20)
      }

      "simply add +1 and -1" in {
        (BitLength(13) + BitLength(-7)).len should be (6)
      }

      "simply add -1 and +1" in {
        (BitLength(-13) + BitLength(7)).len should be (-6)
      }

      "simply add -1 and 1" in {
        (BitLength(-13) + BitLength(-7)).len should be (-20)
      }

      "recognize an overflow" in {
        (BitLength(Double.MaxValue) + BitLength(99)).len should be (Double.MaxValue)
      }

      "recognize an underflow" in {
        (BitLength(Double.MinValue) + BitLength(-99)).len should be (Double.MinValue)
      }
    }

    "using function '-'" should {
      "simply sub +1 and +1" in {
        (BitLength(13) - BitLength(7)).len should be (6)
      }

      "simply sub +1 and -1" in {
        (BitLength(13) - BitLength(-7)).len should be (20)
      }

      "simply sub -1 and +1" in {
        (BitLength(-13) - BitLength(7)).len should be (-20)
      }

      "simply sub -1 and 1" in {
        (BitLength(-13) - BitLength(-7)).len should be (-6)
      }

      "recognize an overflow" in {
        (BitLength(Double.MaxValue) - BitLength(-99)).len should be (Double.MaxValue)
      }

      "recognize an underflow" in {
        (BitLength(Double.MinValue) - BitLength(99)).len should be (Double.MinValue)
      }
    }
  }
}
