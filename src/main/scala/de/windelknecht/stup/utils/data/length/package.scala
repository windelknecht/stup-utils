package de.windelknecht.stup.utils.data

package object length {
  implicit class toDataLength(in: Int) {
    def bit = DataLength(in, DataLengthUnit.bit)
    def bits = bit

    def byte = DataLength(in, DataLengthUnit.byte)
    def bytes = byte

    def Kilobyte = DataLength(in, DataLengthUnit.Kilobyte)
    def kB = Kilobyte

    def Kibibyte = DataLength(in, DataLengthUnit.Kibibyte)
    def KiB = Kibibyte

    def Megabyte = DataLength(in, DataLengthUnit.Megabyte)
    def MB = Megabyte

    def Mebibyte = DataLength(in, DataLengthUnit.Mebibyte)
    def MiB = Mebibyte

    def Gigabyte = DataLength(in, DataLengthUnit.Gigabyte)
    def GB = Gigabyte

    def Gibibyte = DataLength(in, DataLengthUnit.Gibibyte)
    def GiB = Gibibyte

    def Terabyte = DataLength(in, DataLengthUnit.Terabyte)
    def TB = Terabyte

    def Tebibyte = DataLength(in, DataLengthUnit.Tebibyte)
    def TiB = Tebibyte

    def Petabyte = DataLength(in, DataLengthUnit.Petabyte)
    def PB = Petabyte

    def Pebibyte = DataLength(in, DataLengthUnit.Pebibyte)
    def PiB = Pebibyte

    def Exabyte = DataLength(in, DataLengthUnit.Exabyte)
    def EB = Exabyte

    def Exbibyte = DataLength(in, DataLengthUnit.Exbibyte)
    def EiB = Exbibyte

    def Zetabyte = DataLength(in, DataLengthUnit.Zetabyte)
    def ZB = Zetabyte

    def Zebibyte = DataLength(in, DataLengthUnit.Zebibyte)
    def ZiB = Zebibyte

    def Yottabyte = DataLength(in, DataLengthUnit.Yottabyte)
    def YB = Yottabyte

    def Yobibyte = DataLength(in, DataLengthUnit.Yobibyte)
    def YiB = Yobibyte
  }
}
