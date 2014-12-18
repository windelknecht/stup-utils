package de.windelknecht.stup.utils.data.length

import de.windelknecht.stup.utils.coding.Implicits.ByteFormatter
import de.windelknecht.stup.utils.data.length.DataLengthUnit.DataLengthUnit

object DataLength {
  def apply(len: Double, unit: DataLengthUnit) = new DataLength(len = len, unit = unit)
  def apply(len: Int, unit: DataLengthUnit) = new DataLength(len = len.toDouble, unit = unit)
}

class DataLength(
  val len: Double,
  val unit: DataLengthUnit
  ) {
  /**
   * Return length as bits.
   */
  def asBits: Double = len * DataLengthUnit.getMultiplierToBits(unit)

  /**
   * Return length as bytes.
   */
  def asB: Double = math.ceil(asBits / 8)

  /**
   * Return length as kB.
   */
  def asKB: Double = math.ceil(asB / 1000)
  def asKiB: Double = math.ceil(asB / 1024)

  /**
   * Return length as MB.
   */
  def asMB: Double = math.ceil(asKB / 1000)
  def asMiB: Double = math.ceil(asKiB / 1024)

  /**
   * Return length as GB.
   */
  def asGB: Double = math.ceil(asMB / 1000)
  def asGiB: Double = math.ceil(asMiB / 1024)

  /**
   * Return length as TB.
   */
  def asTB: Double = math.ceil(asGB / 1000)
  def asTiB: Double = math.ceil(asGiB / 1024)

  /**
   * Return length as PB.
   */
  def asPB: Double = math.ceil(asTB / 1000)
  def asPiB: Double = math.ceil(asTiB / 1024)

  /**
   * Return length as EB.
   */
  def asEB: Double = math.ceil(asPB / 1000)
  def asEiB: Double = math.ceil(asPiB / 1024)

  /**
   * Return length as ZB.
   */
  def asZB: Double = math.ceil(asEB / 1000)
  def asZiB: Double = math.ceil(asEiB / 1024)

  /**
   * Return length as YB.
   */
  def asYB: Double = math.ceil(asZB / 1000)
  def asYiB: Double = math.ceil(asZiB / 1024)

  override def toString = {
    val bf = new ByteFormatter(asB.toLong)

    unit match {
      case DataLengthUnit.bit => s"$len bits"
      case DataLengthUnit.Yottabyte => s"$len YB"
      case DataLengthUnit.Yobibyte => s"$len YiB"

      case _ => if(DataLengthUnit.isSI(unit)) bf.asHumanReadableSiByte else bf.asHumanReadableByte
    }
  }
}

