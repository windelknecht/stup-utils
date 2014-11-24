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

package de.windelknecht.stup.utils.coding.version

import de.windelknecht.stup.utils.coding.Implicits._

object PatchLevelVersion {
  // fields
  private val fullVersion_r = """(\d+)\.(\d+)-(\d+)""".r
  private val majorVersion_r = """(\d+)""".r
  private val minorVersion_r = """\.(\d+)-(\d+)""".r

  /**
   * Convert from string to version.
   */
  implicit def fromString(s: String): PatchLevelVersion = s match {
    case fullVersion_r(mr, mn, pl) => PatchLevelVersion(major = mr.asInt().getOrElse(0), minor = mn.asInt().getOrElse(0), patchLevel = pl.asInt().getOrElse(0))
    case majorVersion_r(mr) => PatchLevelVersion(major = mr.asInt().getOrElse(0), minor = 0, patchLevel = 0)
    case minorVersion_r(mn, pl) => PatchLevelVersion(major = 0, minor = mn.asInt().getOrElse(0), patchLevel = pl.asInt().getOrElse(0))

    case _ => PatchLevelVersion()
  }
}

case class PatchLevelVersion(
  major: Int = 0,
  minor: Int = 0,
  patchLevel: Int = 0
  )
  extends Version
  with HasMajorMinor
  with Ordered[Version]
  {
  /**
   * Result of comparing `this` with operand `that`.
   *
   * Implement this method to determine how instances of A will be sorted.
   */
  override def compare(that: Version) = {
    that match {
      case v: PatchLevelVersion => compareMajorMinor(v) + patchLevel - v.patchLevel
      case v: TwoNumberVersion  => if (compareMajorMinor(v) == 0) patchLevel else compareMajorMinor(v)

      case _ => 0
    }
  }

  override def toString = s"$major.$minor-$patchLevel"
}
