package de.windelknecht.stup.utils.coding.pipes

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.testkit.{ImplicitSender, DefaultTimeout, TestKit}
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.reflect.ClassTag
import de.windelknecht.stup.utils.coding.pipes.PipeNode.PipeConnected

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 11.04.14
 * Time: 11:31
 *
 */
class PipeSpec
  extends TestKit(ActorSystem("pipeSpecSystem"))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  class PipeNodeImpl[T](
    name: String
    )(implicit classTag: ClassTag[T]) extends PipeNode {
    override val pipeNodeInfo: PipeNodeInfo = PipeNodeInfo[T](name = name)

    override def pipeReceive = {
      case PipeConnected =>
      case m@_ => testActor ! s"$name: $m"
    }
  }
  class SilentPipeNodeImpl[T](
    name: String
    )(implicit classTag: ClassTag[T]) extends PipeNode {
    override val pipeNodeInfo: PipeNodeInfo = PipeNodeInfo[T](name = name)

    override def pipeReceive = {
      case m@_ =>
    }
  }

  "A Pipe" when {
    "connected with 2 different end points" should {
      "receive msg send from pipe 1" in {
        val pipe1 = new PipeNodeImpl("pipe1")
        val pipe2 = new PipeNodeImpl("pipe2")

        new Pipe(pipe1, pipe2)
          .inject(pipe1, "msg_from_pipe1")

        within(1 second) {
          expectMsg(s"pipe2: msg_from_pipe1")
        }
      }

      "receive msg send from pipe 2" in {
        val pipe1 = new PipeNodeImpl("pipe1")
        val pipe2 = new PipeNodeImpl("pipe2")

        new Pipe(pipe1, pipe2)
          .inject(pipe2, "msg_from_pipe2")

        within(1 second) {
          expectMsg(s"pipe1: msg_from_pipe2")
        }
      }

      "pipe1 get 'setPipeConnector' called" in {
        val pipe1 = new SilentPipeNodeImpl("pipe1")
        val pipe2 = new SilentPipeNodeImpl("pipe2")

        new Pipe(pipe1, pipe2)

        pipe1.isPipeConnected should be (right = true)
      }

      "pipe2 get 'setPipeConnector' called" in {
        val pipe1 = new SilentPipeNodeImpl("pipe1")
        val pipe2 = new SilentPipeNodeImpl("pipe2")

        new Pipe(pipe1, pipe2)

        pipe2.isPipeConnected should be (right = true)
      }

      "pipe1 receive PipeTerminated when PipeConnector is stopped" in {
        val pipe1 = new PipeNodeImpl("pipe1")
        val pipe2 = new SilentPipeNodeImpl("pipe2")

        new Pipe(pipe1, pipe2)
          .stop()

        within(1 second) {
          expectMsg(s"pipe1: PipeDisconnected")
        }
      }

      "pipe2 receive PipeTerminated when PipeConnector is stopped" in {
        val pipe1 = new SilentPipeNodeImpl("pipe1")
        val pipe2 = new PipeNodeImpl("pipe2")

        new Pipe(pipe1, pipe2)
          .stop()

        within(1 second) {
          expectMsg(s"pipe2: PipeDisconnected")
        }
      }

      "throw an IllegalArgumentException if the end points have different types" in {
        val pipe1 = new SilentPipeNodeImpl[String]("pipe1")
        val pipe2 = new SilentPipeNodeImpl[Int]("pipe2")

        an [IllegalArgumentException] should be thrownBy { new Pipe(pipe1, pipe2) }
      }
    }

    "connected with the same end point" should {
      "receive msg send from itself" in {
        val pipe = new PipeNodeImpl("pipe")

        new Pipe(pipe, pipe)
          .inject(pipe, "msg_from_pipe")

        within(1 second) {
          expectMsg(s"pipe: msg_from_pipe")
        }
      }

      "receive PipeTerminated when PipeConnector is stopped (only one time)" in {
        val pipe = new PipeNodeImpl("pipe")

        new Pipe(pipe, pipe).stop()

        within(300 millis) {
          expectMsg(s"pipe: PipeDisconnected")
          expectNoMsg()
        }
      }
    }
  }
}
