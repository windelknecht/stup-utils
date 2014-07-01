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

package de.windelknecht.stup.utils.io.file.simple

import de.windelknecht.stup.utils.coding.reactive.Notify
import de.windelknecht.stup.utils.coding.reactive.Notify.NotifyEvent

object DataProvider {
  trait DataProviderEvent extends NotifyEvent
  /**
   * Event notifier for this data provider. Something is happen.
   */
  case object OnZeuch extends DataProviderEvent


  trait DataProviderMsg
  /**
   * Original data has changed. Please reread.
   */
  case object DataChanged extends DataProviderMsg

  /**
   * Data were written, whatever this means..
   */
  case object DataWritten extends DataProviderMsg
}

trait DataProvider {this: Notify=>
  /**
   * Reads all data and return as string list.
   */
  def read(): List[String]

  /**
   * Write the given data
   * @param content is the data to be written
   * @param force write now or at your own timed schedule
   */
  def write(content: String, force: Boolean = false)
}