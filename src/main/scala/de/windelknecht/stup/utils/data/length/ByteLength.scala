package de.windelknecht.stup.utils.data.length

import java.nio.ByteBuffer

import de.windelknecht.stup.utils.data.length.ByteUnit.ByteUnit

import scala.collection.BitSet
import scala.collection.mutable.ArrayBuffer

object ByteLength {
  def apply(bits: BitLength) = {
    var shift = Double.box(bits.len)

    val r1 = java.lang.Double.doubleToRawLongBits(bits.len)
    val r2 = r1.toDouble
    val r3 = BitSet
    val r4 = ByteBuffer.allocateDirect(30).putDouble(bits.len)

//    r4.flip()
//    val r5 = r4.array()
//    val r6 = new ArrayBuffer[Long]()

//    r6 ++= r4.getLong
    r4.flip()
    val r7 = r4.asLongBuffer()
    val r8 = new Array[Long](r7.capacity())
    r7.get(r8)

//    apply(bits, )
    println("")
  }

  def apply(bits: BitLength, unit: ByteUnit) = new ByteLength(bits, unit)
}

class ByteLength(
  private[ByteLength] val bits: BitLength,
  unit: ByteUnit
  ) {
//  def +(that: ByteLength) = ByteLength(bits + that.bits, )
//
//  def -(that: ByteLength) = ByteLength(bits - that.bits, )
}
