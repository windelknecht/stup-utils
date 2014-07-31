package de.windelknecht.stup.utils.coding.reactive

import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor.{Props, Actor, ActorSystem}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import de.windelknecht.stup.utils.coding.reactive.Notify._
import java.util.UUID
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 22.01.14
 * Time: 13:56
 *
 */
trait EBase extends NotifyEvent
case object E1 extends EBase
case object E2 extends EBase
case object E3 extends EBase
case object E4 extends EBase
class A
  extends Actor
  with Notify {
  markThisEventAsPending(E3)

  def receive = notifyReceive orElse default

  def default: PartialFunction[Any, Unit] = {
    case "E1" => fireNotify(E1, "E1")
    case "E2" => fireNotify(E2, "E2")
    case "E3" => fireNotify(E3, "E3")
    case _ =>
  }
}
class B
  extends Notify {
  markThisEventAsPending(E3)

  def fire: PartialFunction[Any, Unit] = {
    case "E1" => fireNotify(E1, "E1")
    case "E2" => fireNotify(E2, "E2")
    case "E3" => fireNotify(E3, "E3")
    case _ =>
  }

  def hasListener = hasRegisteredListener
}
class C
  extends Actor
  with Notify {
  markThisEventAsPending(E3)

  override protected def checkFilter = {
    case (_, m: String, l) => l.contains(m)
  }

  def receive = notifyReceive orElse default

  def default: PartialFunction[Any, Unit] = {
    case "E1" => fireNotify(E1, "E1")
    case "E2" => fireNotify(E2, "E2")
    case "E3" => fireNotify(E3, "E3")
    case "E4" => fireNotify(E4, 4)
    case _ =>
  }
}

class NotifySpec
  extends TestKit(ActorSystem("scriptHandlerSpecSystem"))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  "an actor (no filter)" when {
    "registered on a single event" should {
      "get notified" in {
        val tto = system.actorOf(Props(classOf[A]))

        tto ! NotifyOn(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => testActor ! "E1"
        })
        tto ! "E1"

        within(1 second) {
          expectMsg("E1")
        }
      }

      "be able to unregister itself" in {
        val tto = system.actorOf(Props(classOf[A]))
        val id = UUID.randomUUID()

        tto ! NotifyOn(id, {
          case (E1, "E1") ++ _ => testActor ! "E1"
        })
        tto ! NotifyOff(id)
        tto ! "E1"

        expectNoMsg(1 second)
      }
    }

    "registered on multiple event" should {
      "get notified" in {
        val tto = system.actorOf(Props(classOf[A]))

        tto ! NotifyOn(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => testActor ! "E1"
          case (E2, "E2") ++ _ => testActor ! "E2"
        })
        tto ! "E1"
        tto ! "E2"

        within(1 second) {
          expectMsg("E1")
          expectMsg("E2")
        }
      }

      "take care of event counts" in {
        val tto = system.actorOf(Props(classOf[A]))

        tto ! NotifyOn(UUID.randomUUID(), {
          case (E1, "E1") ++ 1 => testActor ! "E1"
        })
        tto ! "E1"
        tto ! "E1"

        receiveN(1, 1 second)(0) should be ("E1")
      }

      "be able to unregister all msg" in {
        val tto = system.actorOf(Props(classOf[A]))
        val id = UUID.randomUUID()

        tto ! NotifyOn(id, {
          case (E1, "E1") ++ _ => testActor ! "E1"
          case (E2, "E2") ++ _ => testActor ! "E2"
        })
        tto ! NotifyOff(id)
        tto ! "E1"
        tto ! "E2"

        expectNoMsg(1 second)
      }
    }

    "registered on a pending event" should {
      "get notified when fired before register" in {
        val tto = system.actorOf(Props(classOf[A]))
        val id = UUID.randomUUID()

        tto ! "E3"
        expectNoMsg(1 second)
        tto ! NotifyOn(id, {
          case (E3, "E3") ++ _ => testActor ! "E3"
        })

        receiveN(1, 1 second)(0) should be ("E3")
      }

      "get notified when fired after register" in {
        val tto = system.actorOf(Props(classOf[A]))
        val id = UUID.randomUUID()

        tto ! NotifyOn(id, {
          case (E3, "E3") ++ _ => testActor ! "E3"
        })
        expectNoMsg(1 second)
        tto ! "E3"

        within(1 second) {
          expectMsg("E3")
        }
      }
    }
  }

  "a non actor (no filter)" when {
    "registered on a single event" should {
      "get notified" in {
        val tto = new B
        var res = "no"

        tto.registerNotify(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => res = "E1"
        })
        tto.fire("E1")
        res should be ("E1")
      }

      "be able to unregister itself" in {
        val tto = new B
        var res = "no"
        val id = UUID.randomUUID()

        tto.registerNotify(id, {
          case (E1, "E1") ++ _ => res = "E1"
        })
        tto.unregisterNotify(id)
        tto.fire("E1")
        res should be ("no")
      }

      "'hasListener' return false when no listener is registered" in {
        new B().hasListener should be (right = false)
      }

      "'hasListener' return true when at lest one listener is registered" in {
        val tto = new B
        var res = "no"

        tto.registerNotify(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => res = "E1"
        })
        tto.hasListener should be (right = true)
      }

      "'hasListener' return true when more than one listener are registered" in {
        val tto = new B

        tto.registerNotify(UUID.randomUUID(), { case (E1, "E1") ++ _ => })
        tto.registerNotify(UUID.randomUUID(), { case (E1, "E1") ++ _ => })
        tto.registerNotify(UUID.randomUUID(), { case (E1, "E1") ++ _ => })
        tto.registerNotify(UUID.randomUUID(), { case (E1, "E1") ++ _ => })

        tto.hasListener should be (right = true)
      }

      "'hasListener' return true when a foreign listener is attached" in {
        val tto = new B
        val foreign = new B

        tto.forwardEvents(foreign)

        tto.hasListener should be (right = true)
      }
    }

    "registered on multiple event" should {
      "get notified" in {
        val tto = new B
        var res = "no"

        tto.registerNotify(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => res = "E1"
          case (E2, "E2") ++ _ => res = "E2"
        })
        tto.fire("E1")
        res should be ("E1")
        tto.fire("E2")
        res should be ("E2")
      }

      "be able to unregister all msg" in {
        val tto = new B
        var res = "no"
        val id = UUID.randomUUID()

        tto.registerNotify(id, {
          case (E1, "E1") ++ _ => res = "E1"
          case (E2, "E2") ++ _ => res = "E2"
        })
        tto.unregisterNotify(id)
        tto.fire("E1")
        res should be ("no")
        tto.fire("E2")
        res should be ("no")
      }
    }

    "attached to a foreign notifier" should {
      "dispatch fired events (not events masked)" in {
        val tto = new B
        val foreign = new B
        var res = "no"

        tto.forwardEvents(foreign)

        foreign.registerNotify(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => res = "E1"
        })
        tto.fire("E1")
        res should be ("E1")
      }

      "dispatch fired events (1st event in list - not masked)" in {
        val tto = new B
        val foreign = new B
        var res = "no"

        tto.forwardEvents(foreign, E1, E3)

        foreign.registerNotify(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => res = "E1"
          case (E2, "E2") ++ _ => res = "E2"
          case (E3, "E3") ++ _ => res = "E3"
        })
        tto.fire("E1")
        res should be ("E1")
      }

      "dispatch fired events (outmasked event)" in {
        val tto = new B
        val foreign = new B
        var res = "no"

        tto.forwardEvents(foreign, E1, E3)

        foreign.registerNotify(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => res = "E1"
          case (E2, "E2") ++ _ => res = "E2"
          case (E3, "E3") ++ _ => res = "E3"
        })
        tto.fire("E2")
        res should be ("no")
      }

      "dispatch fired events (last event in list - not masked)" in {
        val tto = new B
        val foreign = new B
        var res = "no"

        tto.forwardEvents(foreign, E1, E3)

        foreign.registerNotify(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => res = "E1"
          case (E2, "E2") ++ _ => res = "E2"
          case (E3, "E3") ++ _ => res = "E3"
        })
        tto.fire("E3")
        res should be ("E3")
      }
    }

    "attached and detached to a foreign notifier" should {
      "not dispatch fired events" in {
        val tto = new B
        val foreign = new B
        var res = "no"

        tto.forwardEvents(foreign)
        tto.unForwardEvents(foreign)

        foreign.registerNotify(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => res = "E1"
        })
        tto.fire("E1")
        res should be ("no")
      }
    }
  }

  "an actor (with filter)" when {
    "used with a valid filter" should {
      "get notified if filter matched" in {
        val tto = system.actorOf(Props(classOf[C]))

        tto ! NotifyOn(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => testActor ! "E1"
        },
        "E1")
        tto ! "E1"

        within(1 second) {
          expectMsg("E1")
        }
      }

      "get not notified if filter not matched" in {
        val tto = system.actorOf(Props(classOf[C]))

        tto ! NotifyOn(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => testActor ! "E1"
        },
        "E2")
        tto ! "E1"

        expectNoMsg(1 second)
      }
    }

    "used with an filter (other type)" should {
      "get not notified" in {
        val tto = system.actorOf(Props(classOf[C]))

        tto ! NotifyOn(UUID.randomUUID(), {
          case (E1, "E1") ++ _ => testActor ! "E1"
        },
        13)
        tto ! "E1"

        expectNoMsg(1 second)
      }

      "get notified if filter is not catched" in {
        val tto = system.actorOf(Props(classOf[C]))

        tto ! NotifyOn(UUID.randomUUID(), {
          case (E4, 4) ++ _ => testActor ! "E4"
        },
        "E4")
        tto ! "E4"

        within(1 second) {
          expectMsg("E4")
        }
      }
    }
  }

  "a notifier checked with OnListenerChange" when {
    "penetrated one notifier" should {
      "fire a (OnListenerChange, HasListenerAdded)" in {
        val tto = new B
        var res = "no"

        tto.registerNotify(UUID.randomUUID(), {
          case (OnListenerChanged, HasListenerAdded) ++ _ => res = "add"
        })
        res should be ("add")
      }

      "fire a (OnListenerChange, HasListenerRemoved)" in {
        val tto = new B
        val id = UUID.randomUUID()
        var res = "no"

        tto.registerNotify(UUID.randomUUID(), {
          case (OnListenerChanged, HasListenerRemoved) ++ _ => res = "rem"
        })
        tto.registerNotify(id, { case _ => })
        tto.unregisterNotify(id)

        res should be ("rem")
      }
    }
  }
}
