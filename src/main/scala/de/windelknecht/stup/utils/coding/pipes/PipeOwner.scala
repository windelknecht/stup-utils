package de.pintono.stup.coding.pipes

import java.util.UUID
import scala.collection.mutable
import de.pintono.stup.coding.pipes.PipeNode.{PipeDisconnected, PipeConnected}
import de.pintono.stup.coding.pipes.PipeOwner.{PipeNotifier, OnValueReceive, OnConnectionChanged, OnPipeChange}

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 11.04.14
 * Time: 10:17
 *
 * Dieser trait kann einen oder mehrere PipeConnectors beherbergen.
 */
object PipeOwner {
  sealed trait PipeOwnerEvent
  case object OnConnectionChanged extends PipeOwnerEvent
  case object OnValueReceive extends PipeOwnerEvent

  case class OnPipeChange(pipe: PipeNode, event: PipeOwnerEvent, msg: Any)

  object ?? {
    def unapply(xs: OnPipeChange): Option[((UUID, String, PipeOwnerEvent), Any)] = Some(((xs.pipe.pipeNodeInfo.id, xs.pipe.pipeNodeInfo.name, xs.event), xs.msg))
  }

  type PipeNotifier = PartialFunction[OnPipeChange, Unit]
}

trait PipeOwner {
  // fields
  private val _inPipes = new mutable.HashSet[PipeNode]()
  private val _outPipes = new mutable.HashSet[PipeNode]()

  def inPipeNodes = _inPipes.toList
  def outPipeNodes = _outPipes.toList

  /**
   * Add given in pipes to this object.
   */
  protected def addInPipeNode(pcs: PipeNode*) = {
    add(_inPipes, pcs:_*)

    pcs.foreach{pipe=> pipe.forwardMsgToOwner({
      case PipeConnected    => forwardEvent(OnPipeChange(pipe, OnConnectionChanged, PipeConnected))
      case PipeDisconnected => forwardEvent(OnPipeChange(pipe, OnConnectionChanged, PipeDisconnected))

      case m@_              => forwardEvent(OnPipeChange(pipe, OnValueReceive, m))
    })}
  }

  /**
   * Add given out pipes to this object.
   */
  protected def addOutPipeConnector(pcs: PipeNode*) = add(_outPipes, pcs:_*)

  /**
   * Register a listener for all in pipe events.
   *
   *  portNotifier = {
   *    case (_, "inport", Port.OnConnectionChanged, Port.DisconnectedFrom) ++++ _ =>
   *  }
   */
  protected def pipeListener: PipeNotifier

  /**
   * Connect the given port to one of our pipes with id pipeConnId.
   */
  def connectPipeNode(pipeNodeId: UUID, remotePipeNode: PipeNode) {
    (_inPipes ++ _outPipes)
      .find(_.pipeNodeInfo.id.equals(pipeNodeId)) match {
      case Some(x) => new Pipe(x, remotePipeNode)
      case None => throw new IllegalArgumentException(s"no pipe with id '$pipeNodeId'")
    }
  }

  /**
   * Connect the given port to one of our pipes with id pipeConnName.
   */
  def connectPipeNode(name: String, remotePipeNode: PipeNode) {
    getPipeNode(name) match {
      case Some(x) => new Pipe(x, remotePipeNode)
      case None => throw new IllegalArgumentException(s"no pipe with id '$name'")
    }
  }

  /**
   * Disconnect given port from src port.
   */
  def disconnectPipeNode(id: UUID) {
    getPipeNode(id) match {
      case Some(x) => x.pipeDisconnect()
      case None => throw new IllegalArgumentException(s"no pipe with id '$id'")
    }
  }

  /**
   * Try to find a node by its id.
   */
  def getPipeNode(id: UUID): Option[PipeNode] = (_inPipes ++ _outPipes).find(_.pipeNodeInfo.id.equals(id))

  /**
   * Try to find a node by its name.
   */
  def getPipeNode(name: String): Option[PipeNode] = (_inPipes ++ _outPipes).find(_.pipeNodeInfo.name.equals(name))

  /**
   * Try to find a node by its info.
   */
  def getPipeNode(nodeInfo: PipeNodeInfo): Option[PipeNode] = getPipeNode(nodeInfo.id)

  /**
   * With this function to send a value.
   */
  protected def injectPipeMsg(
    pipeConnId: UUID,
    value: Any
  ) {
    (_inPipes ++ _outPipes)
      .find(_.pipeNodeInfo.id.equals(pipeConnId)) match {
      case Some(x) => x.pipeInject(value)
      case None => throw new IllegalArgumentException(s"no pipe with id '$pipeConnId'")
    }
  }

  /**
   * With this function to send a value.
   */
  protected def injectPipeMsg(
    pipeConnName: String,
    value: Any
  ) {
    (_inPipes ++ _outPipes)
      .find(_.pipeNodeInfo.name.equals(pipeConnName)) match {
      case Some(x) => x.pipeInject(value)
      case None => throw new IllegalArgumentException(s"no pipe with name '$pipeConnName'")
    }
  }

  /**
   * Add given pipes to irgendeine list.
   */
  private def add(list: mutable.HashSet[PipeNode], pipes: PipeNode*) = list ++= pipes

  /**
   * Forward event to this owner class.
   */
  private def forwardEvent(ev: OnPipeChange) = (pipeListener orElse forwardMsgTrash)(ev)

  /**
   * Handler for unwanted events
   */
  private def forwardMsgTrash: PartialFunction[OnPipeChange, Unit] = { case _ => }
}
