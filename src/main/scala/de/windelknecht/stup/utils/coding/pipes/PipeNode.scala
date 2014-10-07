package de.windelknecht.stup.utils.coding.pipes

import de.windelknecht.stup.utils.coding.pipes.PipeNode.{PipeConnected, PipeDisconnected, PipeRx}

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 11.04.14
 * Time: 09:37
 *
 * Ein Pipe-Node ist ein Endpunkt zweier Pipe.
 */
object PipeNode {
  trait PipeNodeEvent
  case object PipeConnected extends PipeNodeEvent
  case object PipeDisconnected extends PipeNodeEvent

  /**
   * Dieser Type wird genutzt, um den Nachrichtenempfang via match-case zu implementieren
   */
  type PipeRx = PartialFunction[Any, Unit]
}

trait PipeNode {
  // fields
  private var _pipe: Option[Pipe] = None
  private var _pipeContainerRx: Option[PipeRx] = None

  /**
   * Muss von der inhalierenden Klasse gesetzt werden.
   */
  val pipeNodeInfo: PipeNodeInfo

  /**
   * Return true/false if there is a remote pipe connected/not connected.
   */
  def isPipeConnected = _pipe != None

  /**
   * Disconnect pipe.
   */
  def pipeDisconnect() {
    _pipe match {
      case Some(x) => x.stop()
      case None =>
    }
  }

  /**
   * Inject a message into the pipe connector.
   * @param msg is the object to send.
   */
  def pipeInject(
    msg: Any
    ) {
    _pipe match {
      case Some(x) => x.inject(this, msg)
      case None => throw new IllegalArgumentException(s"trying to inject a command, but no pipe connected. (msg='$msg')")
    }
  }

  /**
   * Diese Function muss vom Nutzer überschrieben werden.
   * Alle Nachrichten vom Gegenpber laufen hier auf.
   *
   * override def pipeReceive = {
   *   case PipeTerminated => mach was
   *   case anyMessage => do(anyMessage)
   * }
   */
  protected def pipeReceive: PipeRx

  /**
   * Handler für unwanted messages
   */
  private def pipeMsgTrash: PipeRx = { case _ => }

  /**
   * Wird von der pipe aufgerufen, wenn dieser Node verbunden werden soll.
   * @param pipe ist die neue pipe
   */
  private [pipes] def connectPipe(pipe: Pipe) = {
    // bereits connected?
    pipeDisconnect()

    _pipe = Some(pipe)
    sendNotify(PipeConnected)
  }

  /**
   * Connector ist jetzt nicht mehr verbunden.
   */
  private [pipes] def disconnectPipe() = {
    _pipe match {
      case Some(x) => sendNotify(PipeDisconnected)
      case None =>
    }

    _pipe = None
  }

  /**
   * Mit dieser Funtktion kann ein PipeNode-Owner alle hier auflaufenden Nachrichten
   * abgreifen.
   */
  private [pipes] def forwardMsgToOwner(receive: PipeRx) = { _pipeContainerRx = Some(receive) }

  /**
   * Send msg to the owner class.
   */
  private [pipes] def sendNotify(msg: Any) = {
    (_pipeContainerRx.getOrElse(pipeReceive) orElse pipeMsgTrash)(msg)
  }
}
