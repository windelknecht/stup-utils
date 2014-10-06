package de.pintono.stup.coding.pipes

import org.scalatest.{Matchers, WordSpecLike}

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 11.04.14
 * Time: 09:50
 *
 */
class PipeNodeSpec
  extends WordSpecLike
  with Matchers {
  "A PipeNode" when {
    "fresh instantiated" should {
      "return false on 'isPipeConnected'" in {
        PipeNodeImpl().isPipeConnected should be (right = false)
      }

      "throw an IllegalArgumentException when injecting a message" in {
        an [IllegalArgumentException] should be thrownBy { PipeNodeImpl().pipeInject("kl") }
      }
    }

    "when connected" should {
      "isPipeConnected should return true" in {
        val pipe1 = PipeNodeImpl()
        new Pipe(pipe1, PipeNodeImpl())

        pipe1.isPipeConnected should be(right = true)
      }

      "receive a PipeConnected" in {
        val pipe1 = PipeNodeImpl()
        new Pipe(pipe1, PipeNodeImpl())

        pipe1.receivePipeConnected should be(right = true)
      }

      "receive a injected message" in {
        val pipe1 = PipeNodeImpl()
        val pipe2 = PipeNodeImpl()
        new Pipe(pipe1, pipe2).inject(pipe2, "klkl")

        Thread.sleep(300)
        pipe1.receivedMsg should be ("klkl")
        pipe2.receivedMsg should be ("")
      }
    }

    "when disconnected" should {
      "isPipeConnected should return false" in {
        val pipe1 = PipeNodeImpl()
        new Pipe(pipe1, PipeNodeImpl()).stop()

        Thread.sleep(200)
        pipe1.isPipeConnected should be(right = false)
      }

      "receive a PipeDisconnected" in {
        val pipe1 = PipeNodeImpl()
        new Pipe(pipe1, PipeNodeImpl()).stop()

        Thread.sleep(200)
        pipe1.receivePipeDisconnected should be(right = true)
      }

      "throw an IllegalArgumentException when injecting a message" in {
        val pipe1 = PipeNodeImpl()
        new Pipe(pipe1, PipeNodeImpl()).stop()

        an [IllegalArgumentException] should be thrownBy { pipe1.pipeInject("kl") }
      }
    }

    "when reconnected to another PipeNode" should {
      "call the old one" in {
        val pipe1 = PipeNodeImpl()
        val pipe2 = PipeNodeImpl()
        val pipe3 = PipeNodeImpl()

        new Pipe(pipe1, pipe2)
        new Pipe(pipe1, pipe3)

        pipe2.isPipeConnected should be (right = false)
      }
    }
  }
}
