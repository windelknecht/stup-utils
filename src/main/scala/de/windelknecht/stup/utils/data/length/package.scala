package de.windelknecht.stup.utils.data

package object length {
  implicit class toDataLength(in: Int) {
    def bits = DataLength(in, DataLengthUnit.bit)
  }
}
