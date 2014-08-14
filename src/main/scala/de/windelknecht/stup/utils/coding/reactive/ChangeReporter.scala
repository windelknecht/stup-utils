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

object ChangeReporter {
  trait ChangeEvent    extends NotifyEvent
  case object OnChange extends ChangeEvent

  // message classes
  trait ChangeMsg

  /**
   * Message received.
   */
  case class PropertyChange(name: String, oldValue: Any, newValue: Any) extends ChangeMsg
}

trait ChangeReporter {this: Notify=>
  import ChangeReporter._

  /**
   * A few report methods for easier change notifying.
   */
  protected def reportChange(msg: ChangeMsg) = fireNotify(OnChange, msg)

  protected def reportPropertyChange(name: String, oldValue: Any, newValue: Any) = reportChange(PropertyChange(name = name, oldValue = oldValue, newValue = newValue))
}
