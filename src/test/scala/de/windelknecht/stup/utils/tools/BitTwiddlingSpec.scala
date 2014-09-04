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

import org.scalatest.{Matchers, WordSpecLike}

import scala.collection.immutable.BitSet

class BitTwiddlingSpec
  extends WordSpecLike with Matchers {
  "method 'getMostSignificantBit'" when {
    "pass an enum" should {
      "identify no bit set" in {
        object enum extends Enumeration {
          type enum = Value
        }
        BitTwiddling.getMostSignificantBit(enum) should be (None)
      }

      "identify bit 0" in {
        object enum extends Enumeration {
          type enum = Value
          val e1 = Value
        }
        BitTwiddling.getMostSignificantBit(enum) should be (Some(0))
      }

      "identify bit 127" in {
        object enum extends Enumeration {
          type enum = Value
          val e1 = Value
          val e2 = Value(127)
        }
        BitTwiddling.getMostSignificantBit(enum) should be (Some(127))
      }
    }

    "pass a bit set" should {
      "identify no bit set" in {
        BitTwiddling.getMostSignificantBit(BitSet()) should be (None)
      }

      "identify bit 0" in {
        BitTwiddling.getMostSignificantBit(BitSet(0)) should be (Some(0))
      }

      "identify bit 127" in {
        BitTwiddling.getMostSignificantBit(BitSet(127)) should be (Some(127))
      }
    }

    "pass an array of long" should {
      "identify no bit set" in {
        BitTwiddling.getMostSignificantBit(Array(0l, 0l)) should be (None)
      }

      "identify bit 0" in {
        BitTwiddling.getMostSignificantBit(Array((1 << 0).toLong)) should be (Some(0))
      }

      "identify bit 127" in {
        BitTwiddling.getMostSignificantBit(Array(0l, (1<<63).toLong)) should be (Some(127))
      }
    }

    "pass a long" should {
      "identify no bit set" in {
        BitTwiddling.getMostSignificantBit(0x0l) should be (None)
      }

      "identify bit 0" in {
        BitTwiddling.getMostSignificantBit((1 << 0).toLong) should be (Some(0))
      }

      "identify bit 63" in {
        BitTwiddling.getMostSignificantBit((1<<63).toLong) should be (Some(63))
      }
    }

    "pass an int" should {
      "identify no bit set" in {
        BitTwiddling.getMostSignificantBit(0x0) should be (None)
      }

      "identify bit 0" in {
        BitTwiddling.getMostSignificantBit(1 << 0) should be (Some(0))
      }

      "identify bit 31" in {
        BitTwiddling.getMostSignificantBit(1<<31) should be (Some(31))
      }
    }
  }
}
