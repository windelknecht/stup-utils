package de.pintono.stup.coding.pipes

import akka.actor.{Props, ActorSystem, Actor}
import de.pintono.stup.coding.pipes.Pipe.PipeTerminated

object Pipe {
  private[pipes] val pipeSystem = ActorSystem("pipesystem")

  trait PipeEvent
  private[pipes] case object PipeTerminated extends PipeEvent
}

class Pipe(
  sourcePipeNode: PipeNode,
  sinkPipeNode: PipeNode
  ) {
  import Pipe._

  require(sourcePipeNode.pipeNodeInfo.typeInfo.equals(sinkPipeNode.pipeNodeInfo.typeInfo), s"both pipe nodes must have the same type (pipe1='${sourcePipeNode.pipeNodeInfo.typeInfo}', pipe2='${sinkPipeNode.pipeNodeInfo.typeInfo}')")

  // fields
  private val sinkActor = pipeSystem.actorOf(Props(classOf[PipeEntkoppler], sinkPipeNode))
  private val sourceActor = pipeSystem.actorOf(Props(classOf[PipeEntkoppler], sourcePipeNode))

  // ctor
  sourcePipeNode.connectPipe(this)
  if(sourcePipeNode != sinkPipeNode)
    sinkPipeNode.connectPipe(this)

  /**
   * Wird von einer der Pipe-Nodes aufgerufen und sendet die angegebene Message
   * an den 'Gegner'.
   *
   * @param callerPipeNode ist der Node von dem die Nachricht geschickt werden soll
   * @param msg ist die zu verschickende Nachricht
   */
  def inject(
    callerPipeNode: PipeNode,
    msg: Any
    ) {
    callerPipeNode match {
      case `sourcePipeNode` => sinkActor ! msg
      case `sinkPipeNode`   => sourceActor ! msg

      case _ => throw new IllegalArgumentException(s"$callerPipeNode is neither registered as source nor as sink}")
    }
  }

  /**
   * Stop this zeuch.
   */
  def stop() {
    sourceActor ! PipeTerminated
    sinkActor ! PipeTerminated

    sourcePipeNode.disconnectPipe()
    sinkPipeNode.disconnectPipe()
  }
}

/**
 * Dies ist der interne Actor für jeweils Source und Sink und wird genutzt, um
 * das ganze zu entkoppeln.
 * Es kann ja durchaus sein, dass die Abarbeitung einer Nachricht länger dauert und
 * in der Zeit kann eine eventuelle Gegennachricht bereits ausgeliefert werden.
 */
private[pipes] class PipeEntkoppler(
  thisPipeNode: PipeNode
  )
  extends Actor {
  override def receive = {
    case PipeTerminated => context.stop(self)
    case m@_            => thisPipeNode.sendNotify(m)
  }
}
