package de.windelknecht.stup.utils.data.length

object DataLengthUnit
  extends Enumeration {
  type DataLengthUnit = Value

  val bit = Value
  val bits = bit

  val byte = Value(3)
  val bytes = byte
  val B = byte
  val Byte = byte
  val Bytes = byte

  val Kilobyte = Value
  val kB = Kilobyte

  val Kibibyte = Value(10)
  val KiB = Kibibyte

  val Megabyte = Value
  val MB = Megabyte

  val Mebibyte = Value(20)
  val MiB = Mebibyte

  val Gigabyte = Value
  val GB = Gigabyte

  val Gibibyte = Value(30)
  val GiB = Gibibyte

  val Terabyte = Value
  val TB = Terabyte

  val Tebibyte = Value(40)
  val TiB = Tebibyte

  val Petabyte = Value
  val PB = Petabyte

  val Pebibyte = Value(50)
  val PiB = Pebibyte

  val Exabyte = Value
  val EB = Exabyte

  val Exbibyte = Value(60)
  val EiB = Exbibyte

  val Zettabyte = Value
  val ZB = Zettabyte

  val Zebibyte = Value(70)
  val ZiB = Zebibyte

  val Yottabyte = Value
  val YB = Yottabyte

  val Yobibyte = Value(80)
  val YiB = Yobibyte

  // fields
  private val _multiplierToBitMap = Map[DataLengthUnit, Double](
    DataLengthUnit.bit       -> 1,
    DataLengthUnit.byte      -> 8,
    DataLengthUnit.Kilobyte  -> 8000d,
    DataLengthUnit.Kibibyte  -> 8d * (1 << 10),
    DataLengthUnit.Megabyte  -> 8000000d,
    DataLengthUnit.Mebibyte  -> 8d * (1 << 20),
    DataLengthUnit.Gigabyte  -> 8000000000d,
    DataLengthUnit.Gibibyte  -> 8d * (1 << 30),
    DataLengthUnit.Terabyte  -> 8000000000000d,
    DataLengthUnit.Tebibyte  -> 8d * 1024 * 1024 * 1024 * 1024,
    DataLengthUnit.Petabyte  -> 8000000000000000d,
    DataLengthUnit.Pebibyte  -> 8d * 1024 * 1024 * 1024 * 1024 * 1024,
    DataLengthUnit.Exabyte   -> 8000000000000000000d,
    DataLengthUnit.Exbibyte  -> 8d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024,
    DataLengthUnit.Zettabyte -> 8000000000000000000000d,
    DataLengthUnit.Zebibyte  -> 8d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024,
    DataLengthUnit.Yottabyte -> 8000000000000000000000000d,
    DataLengthUnit.Yobibyte  -> 8d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024
  )

  def getMultiplierToBits(unit: DataLengthUnit): Double = _multiplierToBitMap.getOrElse(unit, 0d)

  def isSI(unit: DataLengthUnit): Boolean = unit match {
    case DataLengthUnit.Kilobyte  => true
    case DataLengthUnit.Megabyte  => true
    case DataLengthUnit.Gigabyte  => true
    case DataLengthUnit.Terabyte  => true
    case DataLengthUnit.Petabyte  => true
    case DataLengthUnit.Exabyte   => true
    case DataLengthUnit.Zettabyte => true
    case DataLengthUnit.Yottabyte => true

    case _ => false
  }
}
