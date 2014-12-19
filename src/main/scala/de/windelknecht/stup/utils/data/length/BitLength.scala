package de.windelknecht.stup.utils.data.length

import de.windelknecht.stup.utils.coding.Implicits.ByteFormatter

object BitLength {
  def apply(in: Double): BitLength = new BitLength(in)
  def apply(in: Int): BitLength = BitLength(in.toDouble)
}

class BitLength(
  val len: Double
  ) {
  def +(in: BitLength) = BitLength(len + in.len)
  def -(in: BitLength) = BitLength(len - in.len)

  def toBits = len
  def toBytes = toBits / 8

  def toKiB = toBytes / 1024
  def toMiB = toKiB / 1024
  def toGiB = toMiB / 1024
  def toTiB = toGiB / 1024
  def toPiB = toTiB / 1024
  def toEiB = toPiB / 1024
  def toZiB = toEiB / 1024
  def toYiB = toZiB / 1024

  def toKB = toBytes / 1000
  def toMB = toKB / 1000
  def toGB = toMB / 1000
  def toTB = toGB / 1000
  def toPB = toTB / 1000
  def toEB = toPB / 1000
  def toZB = toEB / 1000
  def toYB = toZB / 1000

  override def toString = toString(asSI = false)

  def toString(
    asSI: Boolean
    ) = {
    val inBytes = toBytes

    if(len < 8) {
      s"$len bits"
    } else {
      if (asSI) new ByteFormatter(inBytes).asHumanReadableSiByte else new ByteFormatter(inBytes).asHumanReadableByte
    }
  }
}
