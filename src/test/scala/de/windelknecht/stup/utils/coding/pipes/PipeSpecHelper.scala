package de.windelknecht.stup.utils.coding.pipes

import de.windelknecht.stup.utils.coding.pipes.PipeNode.{PipeDisconnected, PipeConnected}
import de.windelknecht.stup.utils.coding.pipes.PipeOwner.PipeNotifier

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 22.04.14
 * Time: 12:04
 *
 */
trait PipeSpecHelper

case class PipeNodeImpl(
  descr: PipeNodeInfo = PipeNodeInfo[Int](name = "pipeNode1")
  )
  extends PipeNode
  with PipeSpecHelper {
  override val pipeNodeInfo: PipeNodeInfo = descr

  var receivePipeConnected = false
  var receivePipeDisconnected = false
  var receivedMsg = ""

  /**
   * Diese Function muss vom Nutzer überschrieben werden.
   * Alle Nachrichten vom Gegenpber laufen hier auf.
   */
  override def pipeReceive = {
    case PipeConnected => receivePipeConnected = true
    case PipeDisconnected => receivePipeDisconnected = true
    case m: String => receivedMsg = m
    case _ =>
  }
}

class PipeOwnerImpl(
  ins: List[PipeNode] = List.empty,
  outs: List[PipeNode] = List.empty,
  listener: PipeNotifier = { case _ => }
  ) extends PipeOwner {
  // ctor
  addInPipeNode(ins:_*)
  addOutPipeConnector(outs:_*)

  override protected def pipeListener = listener
}
