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

import scala.util.Random

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 01.11.13
 * Time: 14:29
 *
 */
object RandomHelper {
  def rnd = new Random(new Random().nextInt())

  /**
   * Return a new random array.
   */
  def rndArray(len: Int): Array[Byte] = {
    val ar = new Array[Byte](len)
    rnd.nextBytes(ar)

    ar
  }

  /**
   * Return a new random boolean.
   */
  def rndBool = rnd.nextBoolean()

  /**
   * Return a new random float.
   */
  def rndDouble = rnd.nextDouble()

  /**
   * Return a new random float.
   */
  def rndFloat = rnd.nextFloat()

  /**
   * Return a new random int with upper and lower bounds.
   */
  def rndInt(lower: Int = 1, upper: Int = Int.MaxValue) = rnd.nextInt(upper - lower) + lower

  /**
   * Return a random string with the given length.
   */
  def rndString(length: Int = 100): String = new String((0 to length).map { i=> rnd.nextPrintableChar() }.toArray)
}
