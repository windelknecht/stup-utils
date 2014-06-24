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

object Implicits {
  implicit def anyToOption[T](v: T): Option[T] = Some(v)

  implicit def intToByte(v: Int): Byte = v.toByte
  implicit def intToBool(v: Int): Boolean = if(v == 0) false else true
  implicit def intToLong(v: Int): Long = v.toLong
  implicit def intToShort(v: Int): Short = v.toShort
  implicit def intToString(v: Int): String = v.toString

  implicit def toFloat(v: Int): Float = v.toFloat
  implicit def toFloat(v: Long): Float = v.toFloat

  implicit def stringToInt(v: String) = v.toInt
  implicit def stringToOption(v: String) = Some(v)

  implicit def fun2Runnable(fun: => Unit) = new Runnable { def run() { fun } }

  /**
   * Scala a value.
   */
  implicit class Scale(v: Double) {
    def scaleBy(count: Int): Double = v / count
  }

  /**
   * Utils for string value.
   */
  implicit class StringUtils(value: String) {
    /**
     * Try to convert from string to int and return as Option[Int]:
     *
     * toInt("102").getOrElse(23)
     */
    def asInt(radix: Int = 10): Option[Int] = {
      try {
        Some(Integer.parseInt(value, radix))
      } catch {
        case e: NumberFormatException => None
      }
    }

    /**
     * Try to convert from string to unsigned int and return as Option[Int]:
     */
    def asUInt(radix: Int = 10): Option[Int] = {
      try {
        Some(Integer.parseUnsignedInt(value, radix))
      } catch {
        case e: NumberFormatException => None
      }
    }

    /**
     * Comfort-Funktion um den übergebenen String in eine Option zu packen, oder halt None zurückzugeben,
     * wenn der String leer ist.
     */
    def asOption: Option[String] = if(value.isEmpty) None else Some(value)

    /**
     * Diese Funktion gibt die Anzahl der gleichen Zeichen von String 1 und String 2 zurück.
     */
    def countMatches(s2: String): Int = {
      if (value.isEmpty || s2.isEmpty)
        0
      else if(value(0) == s2(0))
        1 + value.drop(1).countMatches(s2.drop(1))
      else
        0
    }

    /**
     * Returns true if the given string can be converted into an integer.
     */
    def isInt(radix: Int = 10) = {
      try {
        Integer.parseInt(value, radix)
        true
      } catch {
        case e: NumberFormatException => false
      }
    }

    /**
     * Returns true if the given string can be converted into an unsigned integer.
     */
    def isUInt(radix: Int = 10) = {
      try {
        Integer.parseUnsignedInt(value, radix)
        true
      } catch {
        case e: NumberFormatException => false
      }
    }
  }

  /**
   * Format time value into a human readable string.
   */
  implicit class TimeFormatter(v: Double) {
    def asHumanReadableTime = v match {
      case _ if v / 1d > 1.0           => asHumanReadableTime_s
      case _ if v / 0.001d > 1.0       => asHumanReadableTime_ms
      case _ if v / 0.000001d > 1.0    => asHumanReadableTime_µs
      case _ if v / 0.000000001d > 1.0 => asHumanReadableTime_ns
      case _                           => asHumanReadableTime_s
    }

    def asHumanReadableTime_s  = "%.3fs".format(v)
    def asHumanReadableTime_ms = "%.3fms".format(v * 1000)
    def asHumanReadableTime_µs = "%.3fµs".format(v * 1000000)
    def asHumanReadableTime_ns = "%.3fns".format(v * 1000000000)
  }
}
