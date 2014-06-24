package de.windelknecht.stup.utils.coding.reactive

import de.windelknecht.stup.utils.coding.reactive.Notify._
import org.scalatest.{Matchers, BeforeAndAfterAll, WordSpecLike}

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 22.01.14
 * Time: 11:45
 *
 */
class PatternMatchingSpec
  extends WordSpecLike with Matchers with BeforeAndAfterAll {
  sealed trait pmEvent extends NotifyEvent
  case object E1 extends pmEvent
  case object E2 extends pmEvent
  case object E3 extends pmEvent

//        object ++ {
//          def unapply[E <: NotifyEvent](xs: OnNotify[E]): Option[((E, Any), Int)] = Some(((xs.event, xs.msg), xs.count))
//        }
//        s1 match {
//          case (OnE1, s) ++ 1          => println("E1_time_1")
//          case (OnE1, s) ++ t if t < 3 => println("E1_time_lower3")
//          case (OnE1, _) ++ _          => println("E1_time_egal")
//        }

  "on checking with '+'" when {
    "testing fix events" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case E1 + _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg1", 1) match {
            case E1 + _ => "e1"
            case E2 + _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable events" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case e + _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg1", 1) match {
            case E1 + _ => "e1"
            case e + _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing fix count" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 3) match {
            case _ + 3 => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 7) match {
            case _ + 1 => "e1"
            case _ + 7 => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable count" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case _ + t => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 1) match {
            case _ + 3 => "e1"
            case _ + t => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable count, guarded with an if" should {
      "match when lower" in {
        // matching
        (0 until 5).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case _ + t if t < 5 => "e1"
              case _ => "no"
            }) should be ("e1")
        }
        // not matching
        (5 until 100).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case _ + t if t < 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
      }

      "match when equal" in {
        // not matching
        (0 until 5).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case _ + t if t == 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
        // matching
        (
          OnNotify(1, E1, "msg1", 5) match {
            case _ + t if t == 5 => "e1"
            case _ => "no"
          }) should be ("e1")
        // not matching
        (6 until 100).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case _ + t if t == 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
      }

      "match when higher" in {
        // not matching
        (0 until 5).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case _ + t if t > 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
        // matching
        (6 until 100).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case _ + t if t > 5 => "e1"
              case _ => "no"
            }) should be ("e1")
        }
      }
    }
  }

  "on checking with '++'" when {
    "testing fix events" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (E1, _) ++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg1", 1) match {
            case (E1, _) ++ _ => "e1"
            case (E2, _) ++ _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable events" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (e, _) ++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg1", 1) match {
            case (E1, _) ++ _ => "e1"
            case (e, _) ++ _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing fix messages" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (_, "msg1") ++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 1) match {
            case (_, "msg1") ++ _ => "e1"
            case (_, "msg2") ++ _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable messages" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (_, m) ++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 1) match {
            case (_, "msg1") ++ _ => "e1"
            case (_, m) ++ _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing fix count" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 3) match {
            case (_, _) ++ 3 => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 7) match {
            case (_, _) ++ 1 => "e1"
            case (_, _) ++ 7 => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable count" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (_, _) ++ t => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 1) match {
            case (_, _) ++ 3 => "e1"
            case (_, _) ++ t => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable count, guarded with an if" should {
      "match when lower" in {
        // matching
        (0 until 5).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _) ++ t if t < 5 => "e1"
              case _ => "no"
            }) should be ("e1")
        }
        // not matching
        (5 until 100).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _) ++ t if t < 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
      }

      "match when equal" in {
        // not matching
        (0 until 5).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _) ++ t if t == 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
        // matching
        (
          OnNotify(1, E1, "msg1", 5) match {
            case (_, _) ++ t if t == 5 => "e1"
            case _ => "no"
          }) should be ("e1")
        // not matching
        (6 until 100).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _) ++ t if t == 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
      }

      "match when higher" in {
        // not matching
        (0 until 5).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _) ++ t if t > 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
        // matching
        (6 until 100).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _) ++ t if t > 5 => "e1"
              case _ => "no"
            }) should be ("e1")
        }
      }
    }
  }

  "on checking with '+++'" when {
    "testing fix events" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (E1, _, _) +++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg1", 1) match {
            case (E1, _, _) +++ _ => "e1"
            case (E2, _, _) +++ _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable events" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (e, _, _) +++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg1", 1) match {
            case (E1, _, _) +++ _ => "e1"
            case (e, _, _) +++ _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing fix messages" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (_, "msg1", _) +++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 1) match {
            case (_, "msg1", _) +++ _ => "e1"
            case (_, "msg2", _) +++ _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable messages" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (_, m, _) +++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 1) match {
            case (_, "msg1", _) +++ _ => "e1"
            case (_, m, _) +++ _ => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing fix count" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 3) match {
            case (_, _, _) +++ 3 => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 7) match {
            case (_, _, _) +++ 1 => "e1"
            case (_, _, _) +++ 7 => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable count" should {
      "match when correct case is the first one" in {
        (
          OnNotify(1, E1, "msg1", 1) match {
            case (_, _, _) +++ t => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case is not the first one" in {
        (
          OnNotify(1, E2, "msg2", 1) match {
            case (_, _, _) +++ 3 => "e1"
            case (_, _, _) +++ t => "e2"
            case _ => "no"
          }) should be ("e2")
      }
    }

    "testing variable count, guarded with an if" should {
      "match when lower" in {
        // matching
        (0 until 5).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _, _) +++ t if t < 5 => "e1"
              case _ => "no"
            }) should be ("e1")
        }
        // not matching
        (5 until 100).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _, _) +++ t if t < 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
      }

      "match when equal" in {
        // not matching
        (0 until 5).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _, _) +++ t if t == 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
        // matching
        (
          OnNotify(1, E1, "msg1", 5) match {
            case (_, _, _) +++ t if t == 5 => "e1"
            case _ => "no"
          }) should be ("e1")
        // not matching
        (6 until 100).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _, _) +++ t if t == 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
      }

      "match when higher" in {
        // not matching
        (0 until 5).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _, _) +++ t if t > 5 => "e1"
              case _ => "no"
            }) should be ("no")
        }
        // matching
        (6 until 100).foreach{tt: Int =>
          (
            OnNotify(1, E1, "msg1", tt) match {
              case (_, _, _) +++ t if t > 5 => "e1"
              case _ => "no"
            }) should be ("e1")
        }
      }
    }

    "testing sender" should {
      "match when correct case as type" in {
        (
          OnNotify("jkljkl", E1, "msg1", 1) match {
            case (_, _, m: String) +++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }

      "match when correct case with specific value" in {
        (
          OnNotify("jkjkl", E2, "msg2", 1) match {
            case (_, _, "jkjkl") +++ _ => "e1"
            case _ => "no"
          }) should be ("e1")
      }
    }
  }
}
