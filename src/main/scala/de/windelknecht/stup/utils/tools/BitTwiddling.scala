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

package de.windelknecht.stup.utils.tools

import scala.collection
import scala.collection.BitSet

object BitTwiddling {
  import de.windelknecht.stup.utils.coding.Implicits._

  /**
   * Get the most significant bit from an enumeration
   */
  def getMostSignificantBit[T <: Enumeration](
    enum: T
    ): Option[Int] = getMostSignificantBit(enum.values.toBitMask)

  /**
   * Get the most significant bit from a bit set
   */

  /**
   * Get the most significant bit from a bit set
   */
  def getMostSignificantBit(
    bitSet: BitSet
    ): Option[Int] = getMostSignificantBit(bitSet.toBitMask)
  
  /**
   * Get the most significant bit from an array of bit masks
   */
  def getMostSignificantBit(
    bitSet: Array[Long]
    ): Option[Int] = {
    val offset = bitSet.tail.size * 64

    getMostSignificantBit(bitSet.reverse.head) match {
      case Some(x) => x + offset
      case None => None
    }
  }
  
  /**
   * Get the most significant bit from a bit mask (long)
   */
  def getMostSignificantBit(
    bitMask: Long
    ): Option[Int] = {
    val offset = if((bitMask >> 32) == 0) 0 else 32

    getMostSignificantBit((bitMask >> offset).toInt) match {
      case Some(x) => x + offset
      case None => None
    }
  }
  
  /**
   * Get the most significant bit from a bit mask (int)
   */
  def getMostSignificantBit(
    bitMask: Int
    ): Option[Int] = if(bitMask == 0) None else if(bitMask & (1 << 31)) Some(31) else (math.log(bitMask) / math.log(2)).toInt
}
