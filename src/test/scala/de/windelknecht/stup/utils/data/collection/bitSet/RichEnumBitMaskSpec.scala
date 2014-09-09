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

import org.scalatest.{Matchers, WordSpecLike}
import scala.collection.immutable.BitSet

class RichEnumBitMaskSpec
  extends WordSpecLike with Matchers {
  "instance created with 'apply'" should {
    "have a shift of 0" in {
      RichEnumBitMask.apply(TestEnum).shift should be (0)
    }

    "have a mask of (1 << TestEnum.maxId) - 1" in {
      RichEnumBitMask.apply(TestEnum).mask should be ((1 << TestEnum.maxId) - 1)
    }
  }

  "instance created with 'applyWithLen'" should {
    "set passed shift value" in {
      RichEnumBitMask.applyWithLen(TestEnum, shift = 8, len = 20).shift should be (8)
    }

    "set passed len as mask" in {
      RichEnumBitMask.applyWithLen(TestEnum, shift = 8, len = 20).mask should be ((1 << 20) - 1)
    }
  }

  "instance created with 'applyWithMask'" should {
    "set passed shift value" in {
      RichEnumBitMask.applyWithMask(TestEnum, shift = 8, mask = 0x3f).shift should be (8)
    }

    "set passed len as mask" in {
      RichEnumBitMask.applyWithMask(TestEnum, shift = 8, mask = 0x3f).mask should be (0x3f)
    }
  }

  "method 'clear(.)'" when {
    "on empty set" should {
      "return empty bit set" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto.clear()
        tto.toBitSet should be (BitSet())
      }
    }

    "non empty set" should {
      "clear all bits" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto += TestEnum.e3
        tto.clear()
        tto.toBitSet should be (BitSet())
      }
    }
  }

  "method 'contains(.)'" when {
    "shift = 0, len-mask" should {
      "return false emtpy set" in {
        val tto = RichEnumBitMask.apply(TestEnum).contains(TestEnum.e1) should be (right = false)
      }

      "return true on non empty set" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.contains(TestEnum.e2) should be (right = true)
      }
    }

    "shift > 0, len >= msb" should {
      "return false emtpy set" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10).contains(TestEnum.e1) should be (right = false)
      }

      "return true on non empty set" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.contains(TestEnum.e1) should be (right = true)
      }
    }

    "shift > 0, len < msb" should {
      "return false emtpy set" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2).contains(TestEnum.e1) should be (right = false)
      }

      "return true on non empty set (not masked out)" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto += TestEnum.e3
        tto.contains(TestEnum.e2) should be (right = true)
      }

      "return false on non empty set (masked out)" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto += TestEnum.e3
        tto.contains(TestEnum.e3) should be (right = false)
      }
    }
  }

  "method '-=(.)'" when {
    "shift = 0, len-mask" should {
      "remove emtpy set" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto -= TestEnum.e2
        tto.toBitSet should be (BitSet())
      }

      "remove on non empty set" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= TestEnum.e1
        tto.toBitSet should be (BitSet(TestEnum.e2.id))
      }
    }

    "shift > 0, len >= msb" should {
      "remove to empty" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += TestEnum.e2
        tto -= TestEnum.e2
        tto.toBitSet should be (BitSet())
      }

      "remove more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= TestEnum.e2
        tto -= TestEnum.e1
        tto.toBitSet should be (BitSet())
      }
    }

    "shift > 0, len < msb" should {
      "remove to empty" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto -= TestEnum.e2
        tto.toBitSet should be (BitSet())
      }

      "remove more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= TestEnum.e2
        tto -= TestEnum.e1
        tto.toBitSet should be (BitSet())
      }

      "mask out exceeding bits" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e3
        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= TestEnum.e3
        tto -= TestEnum.e2
        tto -= TestEnum.e1
        tto.toBitSet should be (BitSet())
      }
    }
  }

  "method '-=(*)'" when {
    "shift = 0, len-mask" should {
      "remove emtpy set" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto -= TestEnum.e2
        tto.toBitSet should be (BitSet())
      }

      "remove on non empty set" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= (TestEnum.e1, TestEnum.e2)
        tto.toBitSet should be (BitSet())
      }
    }

    "shift > 0, len >= msb" should {
      "remove to empty" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= (TestEnum.e1, TestEnum.e2)
        tto.toBitSet should be (BitSet())
      }

      "remove more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= (TestEnum.e2, TestEnum.e1)
        tto.toBitSet should be (BitSet())
      }
    }

    "shift > 0, len < msb" should {
      "remove to empty" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= (TestEnum.e1, TestEnum.e2)
        tto.toBitSet should be (BitSet())
      }

      "remove more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= (TestEnum.e2, TestEnum.e1)
        tto.toBitSet should be (BitSet())
      }

      "mask out exceeding bits" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e3
        tto += TestEnum.e2
        tto += TestEnum.e1
        tto -= (TestEnum.e2, TestEnum.e1, TestEnum.e3)
        tto.toBitSet should be (BitSet())
      }
    }
  }

  "method '+=(.)'" when {
    "shift = 0, len-mask" should {
      "add one enum" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto += TestEnum.e2
        tto.toBitSet should be (BitSet(TestEnum.e2.id))
      }

      "add more enum" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.toBitSet should be (BitSet(TestEnum.e1.id, TestEnum.e2.id))
      }
    }

    "shift > 0, len >= msb" should {
      "add one enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += TestEnum.e2
        tto.toBitSet should be (BitSet(TestEnum.e2.id + 6))
      }

      "add more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.toBitSet should be (BitSet(TestEnum.e1.id + 6, TestEnum.e2.id + 6))
      }
    }

    "shift > 0, len < msb" should {
      "add one enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto.toBitSet should be (BitSet(TestEnum.e2.id + 6))
      }

      "add more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.toBitSet should be (BitSet(TestEnum.e1.id + 6, TestEnum.e2.id + 6))
      }

      "mask out exceeding bits" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e3
        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.toBitSet should be (BitSet(TestEnum.e1.id + 6, TestEnum.e2.id + 6))
      }
    }
  }

  "method '+=(*)'" when {
    "shift = 0, len-mask" should {
      "add more enum" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto += (TestEnum.e2, TestEnum.e1)
        tto.toBitSet should be (BitSet(TestEnum.e1.id, TestEnum.e2.id))
      }
    }

    "shift > 0, len >= msb" should {
      "add more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += (TestEnum.e2, TestEnum.e1)
        tto.toBitSet should be (BitSet(TestEnum.e1.id + 6, TestEnum.e2.id + 6))
      }
    }

    "shift > 0, len < msb" should {
      "add more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += (TestEnum.e2, TestEnum.e1)
        tto.toBitSet should be (BitSet(TestEnum.e1.id + 6, TestEnum.e2.id + 6))
      }

      "mask out exceeding bits" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += (TestEnum.e3, TestEnum.e2, TestEnum.e1)
        tto.toBitSet should be (BitSet(TestEnum.e1.id + 6, TestEnum.e2.id + 6))
      }
    }
  }

  "method 'add(.)'" when {
    "shift = 0, len-mask" should {
      "add one enum" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto.add(TestEnum.e2)
        tto.toBitSet should be (BitSet(TestEnum.e2.id))
      }

      "add more enum" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto.add(TestEnum.e2)
        tto.add(TestEnum.e1)
        tto.toBitSet should be (BitSet(TestEnum.e1.id, TestEnum.e2.id))
      }
    }

    "shift > 0, len >= msb" should {
      "add one enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto.add(TestEnum.e2)
        tto.toBitSet should be (BitSet(TestEnum.e2.id + 6))
      }

      "add more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto.add(TestEnum.e2)
        tto.add(TestEnum.e1)
        tto.toBitSet should be (BitSet(TestEnum.e1.id + 6, TestEnum.e2.id + 6))
      }
    }

    "shift > 0, len < msb" should {
      "add one enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto.add(TestEnum.e2)
        tto.toBitSet should be (BitSet(TestEnum.e2.id + 6))
      }

      "add more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto.add(TestEnum.e2)
        tto.add(TestEnum.e1)
        tto.toBitSet should be (BitSet(TestEnum.e1.id + 6, TestEnum.e2.id + 6))
      }

      "mask out exceeding bits" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto.add(TestEnum.e3)
        tto.add(TestEnum.e2)
        tto.add(TestEnum.e1)
        tto.toBitSet should be (BitSet(TestEnum.e1.id + 6, TestEnum.e2.id + 6))
      }
    }
  }

  "method 'remove(.)'" when {
    "shift = 0, len-mask" should {
      "remove emtpy set" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto.remove(TestEnum.e2)
        tto.toBitSet should be (BitSet())
      }

      "remove on non empty set" in {
        val tto = RichEnumBitMask.apply(TestEnum)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.remove(TestEnum.e1)
        tto.toBitSet should be (BitSet(TestEnum.e2.id))
      }
    }

    "shift > 0, len >= msb" should {
      "remove to empty" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += TestEnum.e2
        tto.remove(TestEnum.e2)
        tto.toBitSet should be (BitSet())
      }

      "remove more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 10)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.remove(TestEnum.e2)
        tto.remove(TestEnum.e1)
        tto.toBitSet should be (BitSet())
      }
    }

    "shift > 0, len < msb" should {
      "remove to empty" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto.remove(TestEnum.e2)
        tto.toBitSet should be (BitSet())
      }

      "remove more enum" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.remove(TestEnum.e2)
        tto.remove(TestEnum.e1)
        tto.toBitSet should be (BitSet())
      }

      "mask out exceeding bits" in {
        val tto = RichEnumBitMask.applyWithLen(TestEnum, shift = 6, len = 2)

        tto += TestEnum.e3
        tto += TestEnum.e2
        tto += TestEnum.e1
        tto.remove(TestEnum.e3)
        tto.remove(TestEnum.e2)
        tto.remove(TestEnum.e1)
        tto.toBitSet should be (BitSet())
      }
    }
  }
}
