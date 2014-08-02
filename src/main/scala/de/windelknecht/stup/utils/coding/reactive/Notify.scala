/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Heiko Blobner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.windelknecht.stup.utils.coding.reactive

import akka.actor.Actor
import java.util.UUID
import scala.collection.mutable

/**
 * Usage:
 *
 * val id = UUID.randomUUID()
 * o ! NotifyOn[EBase](id, {
 *   case (E1, "E1")         ++  _          => testActor ! "E1" // behandelt das E1 event sooft es eintrifft
 *   case (E2, "E2")         ++  2          => testActor ! "E2" // behandelt das E2 event nur beim 2. Eintreffen
 *   case (E2, _   )         ++  t if t < 3 => testActor ! "Ex" // behandelt das E2 event wenn es die ersten 2 mal bei uns eintrifft
 *
 *   case  E1                +   _          => nur das Event an sich ist interessant
 *   case (E1, "E1", sender) +++ _          => wenn man noch den sender wissen will
 * })
 */
object Notify {
  /**
   * All events must derive from this trait.
   * All sub events must be objects.
   */
  trait NotifyEvent

  /**
   * Gets fired when a listener/user has this model aquired or released.
   */
  case object OnListenerChanged extends NotifyEvent


  sealed trait NotifyMsg
  sealed trait NotifyReq extends NotifyMsg
  /**
   * Register a notify listener for an event.
   *
   * @param id unique listener identifier (used to unregister)
   * @param op partial function with case queries
   */
  case class NotifyOn(id: UUID, op: PartialFunction[OnEventFired, Unit], filter: Any*) extends NotifyReq

  /**
   * Unregister a listener.
   */
  case class NotifyOff(id: UUID) extends NotifyReq

  /**
   * Unregister a listener.
   */
  case class ForwardEvents(foreigner: Notify, events: Any*) extends NotifyReq


  sealed trait NotifyRes extends NotifyMsg
  /**
   * A new listener is watching this model
   */
  case object HasListenerAdded extends NotifyRes
  /**
   * A listener is this not watching anymore. If no more listener is active, it can be freed.
   */
  case object HasListenerRemoved extends NotifyRes


  /**
   * This class is used to hold a fired event.
   *
   * @param sender who fired this event
   * @param event event object
   * @param msg is the message with the information
   * @param count how many times the event is fired
   */
  case class OnEventFired(sender: Any, event: NotifyEvent, msg: Any, count: Int = 1)

  /**
   * Object to provide a match-case operation
   */
  object + {
    def unapply(xs: OnEventFired): Option[(NotifyEvent, Int)] = Some((xs.event, xs.count))
  }
  object ++ {
    def unapply(xs: OnEventFired): Option[((NotifyEvent, Any), Int)] = Some(((xs.event, xs.msg), xs.count))
  }
  object +++ {
    def unapply(xs: OnEventFired): Option[((NotifyEvent, Any, Any), Int)] = Some(((xs.event, xs.msg, xs.sender), xs.count))
  }

  type NotifyRx = PartialFunction[OnEventFired, Unit]

  /**
   * 1. Param = is the NotifyEvent
   * 2. Param = is the message
   * 3. Param = List of filters passed from user code
   *
   * Must return true (filter does not match -> dispatch event) or false (filter matched -> not dispatch event).
   */
  type CheckOp = PartialFunction[(NotifyEvent, Any, List[Any]), Boolean]

  case class ListenerDescr(op: NotifyRx, filter: List[Any])

  implicit def toOpt(notifier: NotifyOn): Option[NotifyOn] = Some(notifier)
}

trait Notify {
  import Notify._

  // fields
  private val _listener        = new mutable.HashMap[UUID, ListenerDescr]()
  private val _firedEvents     = new mutable.HashMap[NotifyEvent, OnEventFired]()
  private val _pendingEvents   = new mutable.HashSet[NotifyEvent]()
  private val _foreignNotifier = new mutable.HashMap[Notify, List[Any]]()

  /**
   * All events fired by this object, will be forwarded to the foreign listeners too.
   * Es kann angegeben werden, welche Events weitergeleitet werden sollen.
   *
   * @param notify is the instance all events will redirected too
   * @param events is a list of event which should be redirected (leave empty to pass all events)
   */
  def forwardEvents(
    notify: Notify,
    events: Any*
    ): Notify = {
    _foreignNotifier.synchronized {
      _foreignNotifier += (notify -> events.toList)
    }
    this
  }

  /**
   * Unregister the foreign notify.
   */
  def unForwardEvents(notify: Notify): Notify = {
    _foreignNotifier.synchronized {
      _foreignNotifier -= notify
    }
    this
  }

  /**
   * Listener can register itself with an optional filter argument (one or more, is variable).
   * So on fire event must be decided if the filter matched or not.
   * 3 partial functions are the decision chain:
   *
   *    empty? -> sub.check? -> fail
   *
   * 1. matches if no filter is defined (pass an event to the listener if its filter is empty)
   * 2. the sub class filters out (pass an event to the listener the deriving class returns a matching true or discard if false)
   *    checkFilter must be overridden by subclass if u want to support filters
   * 3. last link in chain send event in any case
   */
  private   def checkFilterIfEmpty: CheckOp = { case (_, _,l) if l.isEmpty => true }
  protected def checkFilter:        CheckOp = { case _ => false } // if there are filters but this method is not implemented -> to not dispatch the messages
  private   def checkFilterFail:    CheckOp = { case _ => true }

  /**
   * Fire an event -> notify all listener
   * @param event is the source
   * @param msg is the msg to send
   */
  protected def fireNotify(
    event: NotifyEvent,
    msg: Any = Nil
    ) {
    val not = _firedEvents.get(event) match {
      case Some(x)              => OnEventFired(sender = thisRef, event = event, msg = msg, count = x.count + 1)
      case None                 => OnEventFired(sender = thisRef, event = event, msg = msg)
    }
    _firedEvents += (event -> not)

    mergeWithForeignEventDescr(event).foreach(fireThisEvent(_, not))
  }

  /**
   * Fire this single event for this listener.
   * And check if filter matched.
   */
  private def fireThisEvent(
    listener: ListenerDescr,
    not: OnEventFired
  ) {
    if((checkFilterIfEmpty orElse checkFilter orElse checkFilterFail)(not.event, not.msg, listener.filter)) {
      (listener.op orElse handleNot)(not)
    }
  }

  /**
   * Returns true if there are any listeners registered.
   */
  protected def hasRegisteredListener = _listener.nonEmpty || _foreignNotifier.nonEmpty

  /**
   * Mark this event as pending,
   */
  protected def markThisEventAsPending(events: NotifyEvent*) = _pendingEvents ++= events

  /**
   * Handler for unwanted events
   */
  private def handleNot: PartialFunction[OnEventFired, Unit] = { case _ => }

  /**
   * Method to work to handle register/unregister.
   * This function must be called within actor event handler method.
   */
  def notifyReceive: PartialFunction[Any, Unit] = {
    case e: NotifyOn                    => registerNotify(e)
    case e: NotifyOff                   => unregisterNotify(e)
    case ForwardEvents(foreigner, evts) => forwardEvents(foreigner, evts)
  }

  /**
   * Return total of registered listener + foreign notifier.
   */
  protected def registeredListenerCount = _listener.size + _foreignNotifier.size

  /**
   * Register a notify listener.
   */
  def ??(id: UUID, op: NotifyRx, filter: Any*) = registerNotify(id, op, filter:_*)

  /**
   * Register a notify listener.
   * @param id unique listener id
   * @param op partial function, notify callback
   */
  def registerNotify(
    id: UUID,
    op: NotifyRx,
    filter: Any*
    ): Notify = {
    _listener.synchronized {
      val l = ListenerDescr(op, filter.toList)
      _listener += (id -> l)

      // fire pending events
      _firedEvents
        .filter{  case(e,_) => _pendingEvents.contains(e) }
        .foreach{ case(_,n) => fireThisEvent(l, n) }

      fireNotify(OnListenerChanged, HasListenerAdded)
    }
    this
  }
  def registerNotify(n: NotifyOn): Notify = registerNotify(n.id, n.op, n.filter:_*)
  def registerNotify(op: Option[NotifyRx]): Notify =
    op match {
      case Some(x) => registerNotify(UUID.randomUUID(), x)
      case None => this
    }

  /**
   * Unregister a notify listener
   * @param id unique listener id
   */
  def unregisterNotify(id: UUID): Notify = {
    _listener.synchronized {
      _listener -= id

      fireNotify(OnListenerChanged, HasListenerRemoved)
    }
    this
  }
  def unregisterNotify(n: NotifyOff): Notify = unregisterNotify(n.id)

  /**
   * This method is used to return the underlying object self reference.
   */
  private def thisRef = this match {
    case actor: Actor => actor.self
    case _ => this
  }

  /**
   * Diese Funktion mergt unsere Event-Descriptoren mit den foreignern (je nachdom ob der übergebene Event ausmaskiert werden soll oder nicht)
   */
  private def mergeWithForeignEventDescr(event: NotifyEvent) = _listener.map(_._2) ++ _foreignNotifier.filter{case (n,list) => list.contains(event) || list.isEmpty}.flatMap(_._1._listener.values)
}
