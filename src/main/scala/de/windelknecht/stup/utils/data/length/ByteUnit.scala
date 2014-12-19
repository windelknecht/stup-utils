package de.windelknecht.stup.utils.data.length

object ByteUnit
  extends Enumeration {
  type ByteUnit = Value

  val B = Value(0)

  val KiB = Value(10)
  val MiB = Value(20)
  val GiB = Value(30)
  val TiB = Value(40)
  val PiB = Value(50)
  val EiB = Value(60)
  val ZiB = Value(70)
  val YiB = Value(80)

  val KB = Value
  val MB = Value
  val GB = Value
  val TB = Value
  val PB = Value
  val EB = Value
  val ZB = Value
  val YB = Value

//  def from(id: Int) = {
//    apply(id)
//  }
}
