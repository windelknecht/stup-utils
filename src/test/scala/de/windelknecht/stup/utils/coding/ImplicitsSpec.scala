package de.windelknecht.stup.utils.coding

import de.windelknecht.stup.utils.coding.Implicits._
import org.scalatest.{Matchers, WordSpecLike}

/**
 * Created by me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 08.06.14
 * Time: 06:01
 */
class ImplicitsSpec
  extends WordSpecLike with Matchers {
  "on implicit class StringUtils" when {
    "using method 'countMatches'" should {
      "count 0" in {
        "so".countMatches("os") should be (0)
      }
      "return 0, if s1 is empty" in {
        "".countMatches("os") should be (0)
      }
      "return 0, if s2 is empty" in {
        "frfrrf".countMatches("") should be (0)
      }
      "return correct, if s1+s2 are equal" in {
        "sdfres".countMatches("sdfres") should be (6)
      }
      "return correct, if s1.length < s2.length" in {
        "sdf".countMatches("sdfres") should be (3)
      }
      "return correct, if s1.length > s2.length" in {
        "sdfdesde".countMatches("sdf") should be (3)
      }
      "return correct, if s1(x) != s2(x) " in {
        "sdfdesde".countMatches("sdfeffrfr") should be (3)
      }
    }
  }
}
