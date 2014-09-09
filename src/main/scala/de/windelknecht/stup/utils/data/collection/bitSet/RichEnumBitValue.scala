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

package de.windelknecht.stup.utils.data.collection.bitSet

import de.windelknecht.stup.utils.tools.BitTwiddling
import scala.collection.immutable.BitSet
import scala.collection.immutable

object RichEnumBitValue {
  /**
   * Create new instance with this enum as underlying type
   *
   * @param enum this is the one
   * @tparam E type info
   * @return new instance
   */
  def apply[E <: Enumeration](
    enum: E
    ) = applyWithLen(enum, 0, BitTwiddling.getMostSignificantBit(enum.maxId).getOrElse(0) + 1)

  /**
   * Create new instance with this enum as underlying type (with len info as mask)
   *
   * @param enum this is the one
   * @param shift shift the result
   * @tparam E type info
   * @return new instance
   */
  def applyWithLen[E <: Enumeration](
    enum: E,
    shift: Int,
    len: Int
    ) = applyWithMask(enum, shift, (1 << len) - 1)

  /**
   * Create new instance with this enum as underlying type (with given mask as mask)
   *
   * @param enum this is the one
   * @param shift shift the result
   * @param mask mask out these bits
   * @tparam E type info
   * @return new instance
   */
  def applyWithMask[E <: Enumeration](
    enum: E,
    shift: Int,
    mask: Int
    ) = new RichEnumBitValue[E](enum, _shift = shift, _mask = mask)
}

class RichEnumBitValue[E <: Enumeration](
  _enum: E,
  _shift: Int,
  _mask: Int
  ) {
  // fields
  private var _value: Option[E#Value] = None

  def mask = _mask
  def shift = _shift

  /**
   * This method is used to clear out the all values.
   */
  def clear() = _value = None

  /**
   * Returns true if the value is active
   */
  def contains(value: E#Value): Boolean = _value.contains(value)

  /**
   * This method is used to clear out the given value.
   */
  def -=(value: E#Value): this.type = {
    if(contains(value))
      _value = None
    this
  }

  /**
   * This method takes the enum and sets its value.
   */
  def +=(value: E#Value): this.type = {
    if(!maskOut(value))
      _value = Some(value)
    this
  }

  /**
   * Add this value
   */
  def add(value: E#Value) = this += value

  /**
   * Remove this value
   */
  def remove(value: E#Value) = this -= value

  /**
   * Return our bit mask.
   */
  def toBitMask: Array[Long] = toBitSet.toBitMask

  /**
   * Return our bit set.
   */
  def toBitSet: immutable.BitSet = BitSet.fromBitMask(Array(shifted()))

  /**
   * Returns true if value should masked out
   */
  private def maskOut(value: E#Value) = (value.id & _mask) == 0

  /**
   * Shift enum to usable var
   */
  private def shifted(): Long = _value match {
    case Some(x) => x.id << _shift
    case None => 0
  }
}
