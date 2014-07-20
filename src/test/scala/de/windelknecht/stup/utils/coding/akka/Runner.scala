package de.windelknecht.stup.utils.coding.akka

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorSystem}
import de.windelknecht.stup.utils.coding.akka.Reaper.WatchMe

/**
 * Created by me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 20.07.14
 * Time: 21:44
 */
class A extends Reaper {
  // Derivations need to implement this method. It's the
  override def allSoulsReaped() = println("reaped")

  // Watch and check for termination
  receiver {
    case m: String => println(s"A receive $m")

    case _ => super.receive
  }
}

object Runner {
  def main(args: Array[String]) {
    val actorSystem = ActorSystem("test")

    val actor = actorSystem.actorOf(Props(classOf[A]))

    actor ! "Hello"
    actor ! WatchMe(null)
  }
}
