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

package de.windelknecht.stup.utils.coding

import collection.mutable
import de.windelknecht.stup.utils.coding.EventEntpreller._
import de.windelknecht.stup.utils.coding.reactive.Notify
import de.windelknecht.stup.utils.coding.reactive.Notify.{NotifyRx, NotifyEvent}
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.Timer
import scala.concurrent.duration.FiniteDuration

object EventEntpreller {
  trait eeEvent extends NotifyEvent
  case object OnTimerExpired extends eeEvent

  trait eeReq

  trait eeRes
  case class EventDescr(evt: AnyRef, data: Any)
}

class EventEntpreller(
  descr: Map[AnyRef, FiniteDuration],
  notifier: NotifyRx
  )
  extends Notify {
  case class TimerData(timer: Timer, Data: Any)

  // fields
  private val _eventToTimer = new mutable.HashMap[AnyRef, TimerData]()

  // ctor
  registerNotify(Some(notifier))

  /**
   * Für den angegebenen Event soll ein Timer aufgesetzt werden (wenn noch nicht getan).
   */
  def entprell(
    evt: AnyRef,
    data: Any
    ) {
    descr.get(evt) match {
      case Some(x) => startTimer(evt, data, x)
      case None    => fireNotify(OnTimerExpired, EventDescr(evt, data))
    }
  }

  /**
   * Shutdown everything.
   */
  def shutdown() = {
    _eventToTimer.foreach(_._2.timer.stop())
  }

  /**
   * Timer aufsetzen wenn dieser noch nicht gestartet wurde..
   */
  private def startTimer(
    evt: AnyRef,
    data: Any,
    durationInMS: FiniteDuration
    ) {
    if(_eventToTimer.contains(evt))
      return

    val timer = new javax.swing.Timer(durationInMS.length.toInt, new ActionListener {
      def actionPerformed(e: ActionEvent) {
        this.synchronized {
          _eventToTimer -= evt
        }
        fireNotify(OnTimerExpired, EventDescr(evt, data))
      }
    })

    _eventToTimer += (evt -> TimerData(timer, data))

    timer.setRepeats(false)
    timer.setInitialDelay(durationInMS.length.toInt)
    timer.start()
  }
}
