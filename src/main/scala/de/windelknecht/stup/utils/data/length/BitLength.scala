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

  override def toString = toString(asSI = false)

  def toString(
    asSI: Boolean
    ) = {
    if(len < 8) {
      s"$len bits"
    } else if(len > 1.0E24) {
      if (asSI) new ByteFormatter(len.toLong).asHumanReadableSiByte else new ByteFormatter(len.toLong).asHumanReadableByte
    } else {
      s"${len / 1.0E24} ${if (asSI) "YB" else "YiB"}"
    }
  }
}
