package de.windelknecht.stup.utils.tools

import org.scalatest.{Matchers, WordSpecLike}

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 22.05.14
 * Time: 08:04
 *
 */
class StopWatchSpec
  extends WordSpecLike with Matchers {
  "called method 'diff' on 2 rounds" when {
    "both round-stamps exist" should {
      "return time in ms between 1st round-call and 3rd round-call" in {
        val sw = createCountingStopWatch()

        sw.round(1)
        sw.round(2)
        sw.round(3)
        sw.diff(1, 3) should be (2)
      }
    }

    "1st round-stamp doesnt exist" should {
      "return -1" in {
        val sw = createCountingStopWatch()

        sw.round(2)
        sw.round(3)
        sw.diff(1, 3) should be (-1)
      }
    }

    "2nd round-stamp doesnt exist" should {
      "return -1" in {
        val sw = createCountingStopWatch()

        sw.round(1)
        sw.round(2)
        sw.diff(1, 3) should be (-1)
      }
    }
  }

  "called method 'diff' on 1 round" when {
    "watch is started" should {
      "return time in ms between start-call and 1st round-call" in {
        val sw = createCountingStopWatch()

        sw.round(1)
        sw.diff(1) should be (1)
      }

      "return absolute time in ms between start-call and 2nd round-call" in {
        val sw = createCountingStopWatch()

        sw.round(1)
        sw.round(2)
        sw.diff(2, getAbsolute = true) should be (2)
      }

      "return relative time in ms between 1st round-call and 2nd round-call" in {
        val sw = createCountingStopWatch()

        sw.round(1)
        sw.round(2)
        sw.diff(2) should be (1)
      }

      "return -1 if round doesnt exist" in {
        val sw = createCountingStopWatch()

        sw.start()
        sw.diff(2) should be (-1)
      }
    }
  }

  "called method 'calcTotal'" when {
    "fresh instantiated'" should {
      "return 0" in {
        new StopWatch().diffTotal should be (0)
      }
    }

    "watch is started" should {
      "return time in ms between start-call and calcTotal-call" in {
        val sw = createCountingStopWatch()

        sw.start()
        sw.diffTotal should be (1)
      }
    }

    "watch is stopped" should {
      "return time in ms between start-call and stop-call" in {
        val sw = createCountingStopWatch()

        sw.start()
        sw.stop()
        sw.diffTotal should be (1)
      }
    }

    "several round() between start and stop" should {
      "return time in ms between start-call and stop-call" in {
        val sw = createCountingStopWatch()

        sw.start()
        (0 to 10).foreach(i=> sw.round(i))
        sw.stop()
        sw.diffTotal should be (12)
      }
    }
  }

  "called method 'round'" when {
    "can catch call after stopped" should {
      "return correct total" in {
        val sw = createCountingStopWatch()

        sw.start()
        sw.stop()
        sw.round(1)
        sw.diff(1, getAbsolute = true) should be (-1)
      }
    }
  }

  "called method 'start'" when {
    "can catch a 2nd call" should {
      "return correct total" in {
        val sw = createCountingStopWatch()

        sw.start()
        sw.stop()
        sw.start()
        sw.start()
        sw.start()
        sw.diffTotal should be (1)
      }
    }
  }

  "called method 'stop'" when {
    "can catch a 2nd call" should {
      "return correct total" in {
        val sw = createCountingStopWatch()

        sw.start()
        sw.stop()
        sw.stop()
        sw.stop()
        sw.stop()
        sw.stop()
        sw.diffTotal should be (1)
      }
    }
  }

  private def createCountingStopWatch() = {
    var cnt = 0

    new StopWatch({
      cnt += 1
      cnt
    })
  }
}
