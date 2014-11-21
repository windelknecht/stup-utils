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
  private val r = """(\d+)\.(\d+)-(\d+)""".r

  implicit def fromString(s: String): PatchLevelVersion = s match {
    case r(mr, mn, pl) => PatchLevelVersion(major = mr.asInt().getOrElse(0), minor = mn.asInt().getOrElse(0), patchLevel = pl.asInt().getOrElse(0))
  }
}

case class PatchLevelVersion(
  major: Int,
  minor: Int,
  patchLevel: Int
  )
  extends Version
  with Ordered[PatchLevelVersion]
  {
  /**
   * Result of comparing `this` with operand `that`.
   *
   * Implement this method to determine how instances of A will be sorted.
   */
  override def compare(that: PatchLevelVersion) = major - that.major + minor - that.minor + patchLevel - that.patchLevel

  override def toString = s"$major.$minor-$patchLevel"
}
