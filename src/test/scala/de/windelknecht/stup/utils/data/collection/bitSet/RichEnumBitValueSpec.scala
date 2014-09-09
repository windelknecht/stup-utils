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

class RichEnumBitValueSpec
  extends WordSpecLike with Matchers {
  "instance created with 'apply'" should {
    "have a shift of 0" in {
      RichEnumBitValue.apply(TestEnum).shift should be (0)
    }

    "have a mask of 0x7" in {
      RichEnumBitValue.apply(TestEnum).mask should be (0x7)
    }
  }

  "instance created with 'applyWithLen'" should {
    "set passed shift value" in {
      RichEnumBitValue.applyWithLen(TestEnum, shift = 8, len = 20).shift should be (8)
    }

    "set passed len as mask" in {
      RichEnumBitValue.applyWithLen(TestEnum, shift = 8, len = 20).mask should be ((1 << 20) - 1)
    }
  }

  "instance created with 'applyWithMask'" should {
    "set passed shift value" in {
      RichEnumBitValue.applyWithMask(TestEnum, shift = 8, mask = 0x3f).shift should be (8)
    }

    "set passed len as mask" in {
      RichEnumBitValue.applyWithMask(TestEnum, shift = 8, mask = 0x3f).mask should be (0x3f)
    }
  }
}
