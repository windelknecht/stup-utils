import akka.actor.{Actor, ActorRef, Terminated}
import scala.collection.mutable.ArrayBuffer

/**
 * Code from http://letitcrash.com/post/30165507578/shutdown-patterns-in-akka-2
 *
 * It is also published on gist.github.com.
 */
object Reaper {
  // Used by others to register an Actor for watching
  case class WatchMe(ref: ActorRef)
}

abstract class Reaper extends Actor {
  import Reaper._

  // Keep track of what we're watching
  val watched = ArrayBuffer.empty[ActorRef]

  // Derivations need to implement this method. It's the
  // hook that's called when everything's dead
  def allSoulsReaped(): Unit

  // Watch and check for termination
  final def receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref
    case Terminated(ref) =>
      watched -= ref
      if (watched.isEmpty) allSoulsReaped()
  }
}
