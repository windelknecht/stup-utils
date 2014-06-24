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

import de.windelknecht.stup.utils.coding.reactive.Notify.NotifyEvent

object RunLevel {
  trait RunLevelEvent    extends NotifyEvent
  case object OnRunLevel extends RunLevelEvent

  // message classes
  trait RunLevelMsg

  /**
   * Object is in init phase.
   */
  case object Init                         extends RunLevelMsg

  /**
   * Object is up and ready to work.
   */
  case object Running                      extends RunLevelMsg

  /**
   * Object is being shut down.
   */
  case object BeforeShutdown               extends RunLevelMsg

  /**
   * Object is shut down.
   */
  case class  AfterShutdown(cause: String) extends RunLevelMsg

  /**
   * Object is in an error state.
   */
  case class  Failure(err: String)         extends RunLevelMsg
}

trait RunLevel {this: Notify=>
  import RunLevel._

  /**
   * A few report methods for easier run level notifying.
   */
  protected def reportRunLevel(msg: RunLevelMsg) = fireNotify(OnRunLevel, msg)

  protected def reportInit          ()                   = reportRunLevel(Init)
  protected def reportRunning       ()                   = reportRunLevel(Running)
  protected def reportBeforeShutdown()                   = reportRunLevel(BeforeShutdown)
  protected def reportAfterShutdown (cause: String = "") = reportRunLevel(AfterShutdown(cause))
  protected def reportFailure       (err: String = "")   = reportRunLevel(Failure(err))

  // ctor code
  markThisEventAsPending(OnRunLevel)
  reportInit()
}
