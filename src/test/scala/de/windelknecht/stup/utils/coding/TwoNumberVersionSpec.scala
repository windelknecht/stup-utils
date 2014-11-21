package de.windelknecht.stup.utils.coding

import de.windelknecht.stup.utils.coding.version.TwoNumberVersion
import org.scalatest.{Matchers, WordSpec}

/**
 * Created by me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 31.07.13
 * Time: 21:57
 */
class TwoNumberVersionSpec
  extends WordSpec
  with Matchers {
  "implicit def toVersion(:String)" should {
    "convert v1.5" in {
      val ver: TwoNumberVersion = "v1.5"

      ver.major should be (1)
      ver.minor should be (5)
    }
    "convert v.8" in {
      val ver: TwoNumberVersion = "v.8"

      ver.major should be (0)
      ver.minor should be (8)
    }
    "convert v8" in {
      val ver: TwoNumberVersion = "v8"

      ver.major should be (8)
      ver.minor should be (0)
    }
    "convert v07.0034" in {
      val ver: TwoNumberVersion = "v07.0034"

      ver.major should be (7)
      ver.minor should be (34)
    }

    "not convert v-28.23" in {
      val ver: TwoNumberVersion = "v-28.23"

      ver.major should be (0)
      ver.minor should be (0)
    }
    "not convert v-28.-23" in {
      val ver: TwoNumberVersion = "v-28.-23"

      ver.major should be (0)
      ver.minor should be (0)
    }
    "not convert v-28" in {
      val ver: TwoNumberVersion = "v-28"

      ver.major should be (0)
      ver.minor should be (0)
    }
    "not convert v28.-23" in {
      val ver: TwoNumberVersion = "v28.-23"

      ver.major should be (0)
      ver.minor should be (0)
    }
    "not convert v.-23" in {
      val ver: TwoNumberVersion = "v.-23"

      ver.major should be (0)
      ver.minor should be (0)
    }
  }

  "comparing two version" should {
    "return -1 on 0.1 vs. 1.1 " in {
      TwoNumberVersion(0, 1) compare TwoNumberVersion(1, 1) should be (-1)
    }

    "return  1 on 1.1 vs. 0.1 " in {
      TwoNumberVersion(1, 1) compare TwoNumberVersion(0, 1) should be (1)
    }

    "return  0 on 1.1 vs. 1.1 " in {
      TwoNumberVersion(1, 1) compare TwoNumberVersion(1, 1) should be (0)
    }

    "return -1 on 1.0 vs. 1.1 " in {
      TwoNumberVersion(1, 0) compare TwoNumberVersion(1, 1) should be (-1)
    }

    "return  1 on 1.2 vs. 1.1 " in {
      TwoNumberVersion(1, 2) compare TwoNumberVersion(1, 1) should be (1)
    }

    "return  0 on 1.3 vs. 1.3 " in {
      TwoNumberVersion(1, 3) compare TwoNumberVersion(1, 3) should be (0)
    }
  }
}
