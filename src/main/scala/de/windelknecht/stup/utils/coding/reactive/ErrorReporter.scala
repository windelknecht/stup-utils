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

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 07.08.14
 * Time: 15:41
 *
 */
object ErrorReporter {
  trait ErrorEvent    extends NotifyEvent
  case object OnError extends ErrorEvent

  // message classes
  trait ErrorMsg

  /**
   * A really serious error.
   * Normal program flow will be disturbed.
   */
  case class Critical(err: Any) extends ErrorMsg

  /**
   * A error.
   */
  case class Error(err: Any) extends ErrorMsg

  /**
   * A warning. just feel you informed.
   */
  case class Warning(err: Any) extends ErrorMsg
}

trait ErrorReporter {this: Notify=>
  import ErrorReporter._

  /**
   * A few report methods for easier traffic notifying.
   */
  protected def reportError(msg: ErrorMsg) = fireNotify(OnError, msg)

  protected def reportCriticalErr(msg: Any) = reportError(Critical(msg))
  protected def reportErrorErr(msg: Any) = reportError(Error(msg))
  protected def reportWarningErr(msg: Any) = reportError(Warning(msg))
}
