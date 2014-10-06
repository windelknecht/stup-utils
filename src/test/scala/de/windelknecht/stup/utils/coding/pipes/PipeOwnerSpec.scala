package de.pintono.stup.coding.pipes

import org.scalatest.{Matchers, WordSpecLike}

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 22.04.14
 * Time: 11:51
 *
 */
class PipeOwnerSpec
  extends WordSpecLike
  with Matchers {
  "A PipeOwner" when {
    "fresh instantiated" should {
      "do not have any IN nodes" in {
        new PipeOwnerImpl().inPipeNodes.size should be (0)
      }

      "do not have any OUT nodes" in {
        new PipeOwnerImpl().outPipeNodes.size should be (0)
      }
    }

    "adding nodes" should {
      "have added all IN nodes" in {
        val l = List(
          PipeNodeImpl(PipeNodeInfo[Int](name = "inNode1")),
          PipeNodeImpl(PipeNodeInfo[Int](name = "inNode2")),
          PipeNodeImpl(PipeNodeInfo[Int](name = "inNode3"))
        )

        val po = new PipeOwnerImpl(ins = l)

        po.inPipeNodes.size should be (3)
        po.inPipeNodes.sortBy(_.pipeNodeInfo.name).toList(0) should be (l(0))
        po.inPipeNodes.sortBy(_.pipeNodeInfo.name).toList(1) should be (l(1))
        po.inPipeNodes.sortBy(_.pipeNodeInfo.name).toList(2) should be (l(2))
      }

      "have added all OUT nodes" in {
        val l = List(
          PipeNodeImpl(PipeNodeInfo[Int](name = "outNode1")),
          PipeNodeImpl(PipeNodeInfo[Int](name = "outNode2")),
          PipeNodeImpl(PipeNodeInfo[Int](name = "outNode3"))
        )

        val po = new PipeOwnerImpl(outs = l)

        po.outPipeNodes.size should be (3)
        po.outPipeNodes.sortBy(_.pipeNodeInfo.name).toList(0) should be (l(0))
        po.outPipeNodes.sortBy(_.pipeNodeInfo.name).toList(1) should be (l(1))
        po.outPipeNodes.sortBy(_.pipeNodeInfo.name).toList(2) should be (l(2))
      }
    }

    "connecting nodes" should {
      "connect the right remote port (by node id)" in {
        val ins = List(
          PipeNodeImpl(PipeNodeInfo[Int](name = "inNode1")),
          PipeNodeImpl(PipeNodeInfo[Int](name = "inNode2")),
          PipeNodeImpl(PipeNodeInfo[Int](name = "inNode3"))
        )
        val outs = List(
          PipeNodeImpl(PipeNodeInfo[Int](name = "outNode1")),
          PipeNodeImpl(PipeNodeInfo[Int](name = "outNode2")),
          PipeNodeImpl(PipeNodeInfo[Int](name = "outNode3"))
        )

        val po = new PipeOwnerImpl(ins = ins, outs = outs)

        po.connectPipeNode(po.getPipeNode("inNode1").get.pipeNodeInfo.id, po.getPipeNode("outNode1").get)
      }
    }

    "getting nodes by name" should {
      "return the correct existing IN node" in {
        val ins = Map(
          "in1" -> PipeNodeImpl(PipeNodeInfo[Int](name = "inNode1")),
          "in2" -> PipeNodeImpl(PipeNodeInfo[Int](name = "inNode2")),
          "in3" -> PipeNodeImpl(PipeNodeInfo[Int](name = "inNode3"))
        )
        val outs = Map(
          "out1" -> PipeNodeImpl(PipeNodeInfo[Int](name = "outNode1")),
          "out2" -> PipeNodeImpl(PipeNodeInfo[Int](name = "outNode2")),
          "out3" -> PipeNodeImpl(PipeNodeInfo[Int](name = "outNode3"))
        )

        new PipeOwnerImpl(ins = ins.values.toList, outs = outs.values.toList).getPipeNode("inNode1").get should be (ins("in1"))
      }

      "return the correct existing OUT node" in {
        val ins = Map(
          "in1" -> PipeNodeImpl(PipeNodeInfo[Int](name = "inNode1")),
          "in2" -> PipeNodeImpl(PipeNodeInfo[Int](name = "inNode2")),
          "in3" -> PipeNodeImpl(PipeNodeInfo[Int](name = "inNode3"))
        )
        val outs = Map(
          "out1" -> PipeNodeImpl(PipeNodeInfo[Int](name = "outNode1")),
          "out2" -> PipeNodeImpl(PipeNodeInfo[Int](name = "outNode2")),
          "out3" -> PipeNodeImpl(PipeNodeInfo[Int](name = "outNode3"))
        )

        new PipeOwnerImpl(ins = ins.values.toList, outs = outs.values.toList).getPipeNode("outNode2").get should be (outs("out2"))
      }

      "return None if not exist (none empty list)" in {
        val ins = Map(
          "in1" -> PipeNodeImpl(PipeNodeInfo[Int](name = "inNode1")),
          "in2" -> PipeNodeImpl(PipeNodeInfo[Int](name = "inNode2")),
          "in3" -> PipeNodeImpl(PipeNodeInfo[Int](name = "inNode3"))
        )
        val outs = Map(
          "out1" -> PipeNodeImpl(PipeNodeInfo[Int](name = "outNode1")),
          "out2" -> PipeNodeImpl(PipeNodeInfo[Int](name = "outNode2")),
          "out3" -> PipeNodeImpl(PipeNodeInfo[Int](name = "outNode3"))
        )

        new PipeOwnerImpl(ins = ins.values.toList, outs = outs.values.toList).getPipeNode("outNrfrfrfode2") should be (None)
      }

      "return None if not exist (empty list)" in {
        new PipeOwnerImpl().getPipeNode("outNode2") should be (None)
      }
    }
  }
}
