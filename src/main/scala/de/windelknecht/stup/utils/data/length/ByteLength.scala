package de.windelknecht.stup.utils.data.length

import de.windelknecht.stup.utils.data.length.ByteUnit.ByteUnit
import de.windelknecht.stup.utils.tools.BitTwiddling

object ByteLength {
  def apply(bits: BitLength) = new ByteLength(bits)

  /**
   * Return the best matching IEC unit for the given bit length
   */
  def getBestMatchingIECUnit(
    in: BitLength
    ): ByteUnit = {
    val r1 = in.toBytes
    val r2 = (r1 % math.pow(2, 64)).toLong // lower long
    val r3 = (r1 / math.pow(2, 64)).toLong // upper long
    val r4 = Array(r2, r3)
    val r5 = BitTwiddling.getMostSignificantBit(r4)

    try {
      ByteUnit(r5.getOrElse(0))
    } catch {
      case e: NoSuchElementException => ByteUnit.B
    }
  }

  /**
   * Return the best matching IEC unit for the given bit length
   */
  def getBestMatchingSIUnit(
    in: BitLength
    ): ByteUnit = {
    in.toBytes match {
      case i: Double if 0 == i % (1 YB).toBytes => ByteUnit.YB
      case i: Double if 0 == i % (1 ZB).toBytes => ByteUnit.ZB
      case i: Double if 0 == i % (1 EB).toBytes => ByteUnit.EB
      case i: Double if 0 == i % (1 PB).toBytes => ByteUnit.PB
      case i: Double if 0 == i % (1 TB).toBytes => ByteUnit.TB
      case i: Double if 0 == i % (1 GB).toBytes => ByteUnit.GB
      case i: Double if 0 == i % (1 MB).toBytes => ByteUnit.MB
      case i: Double if 0 == i % (1 KB).toBytes => ByteUnit.KB

      case _ => ByteUnit.B
    }
  }
}

class ByteLength(
  private[ByteLength] val bits: BitLength
  ) {
  def +(that: ByteLength) = ByteLength(bits + that.bits)
  def -(that: ByteLength) = ByteLength(bits - that.bits)

  def isBit: Boolean = bits.len % 8 != 0

  def toBits = bits.toBits
  def toByte = bits.toBytes
  def toBytes = toByte

  def toKiB = toByte / 1024
  def toMiB = toKiB / 1024
  def toGiB = toMiB / 1024
  def toTiB = toGiB / 1024
  def toPiB = toTiB / 1024
  def toEiB = toPiB / 1024
  def toZiB = toEiB / 1024
  def toYiB = toZiB / 1024

  def toKB = toByte / 1000
  def toMB = toKB / 1000
  def toGB = toMB / 1000
  def toTB = toGB / 1000
  def toPB = toTB / 1000
  def toEB = toPB / 1000
  def toZB = toEB / 1000
  def toYB = toZB / 1000
}
