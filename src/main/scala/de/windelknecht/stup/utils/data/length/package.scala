package de.windelknecht.stup.utils.data

package object length {
  implicit class fromBitLength(in: BitLength) {
    def asBits = in.len
    def asBytes = asBits / 8
  }

  implicit class fromBitLengthToIEC(in: BitLength) {
    def asKiB = new fromBitLength(in).asBytes / 1024
    def asMiB = asKiB / 1024
    def asGiB = asMiB / 1024
    def asTiB = asGiB / 1024
    def asPiB = asTiB / 1024
    def asEiB = asPiB / 1024
    def asZiB = asEiB / 1024
    def asYiB = asZiB / 1024
  }

  implicit class fromBitLengthToSI(in: BitLength) {
    def asKB = new fromBitLength(in).asBytes / 1000
    def asMB = asKB / 1000
    def asGB = asMB / 1000
    def asTB = asGB / 1000
    def asPB = asTB / 1000
    def asEB = asPB / 1000
    def asZB = asEB / 1000
    def asYB = asZB / 1000
  }

  implicit class toBitLength(in: Double) {
    def bit = BitLength(in)
    def bits = bit
    def Bit = bit
    def Bits = bit

    def byte = BitLength(in * 8)
    def bytes = byte
    def B = byte
    def Byte = byte
    def Bytes = byte
  }

  implicit class toBitLengthFromIEC(in: Double) {
    def Kibibyte = BitLength(in * 8 * 1024)
    def KiB = Kibibyte

    def Mebibyte = BitLength(in * 8 * 1024 * 1024)
    def MiB = Mebibyte

    def Gibibyte = BitLength(in * 8 * 1024 * 1024 * 1024)
    def GiB = Gibibyte

    def Tebibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024)
    def TiB = Tebibyte

    def Pebibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024 * 1024)
    def PiB = Pebibyte

    def Exbibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024)
    def EiB = Exbibyte

    def Zebibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024)
    def ZiB = Zebibyte

    def Yobibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024)
    def YiB = Yobibyte
  }

  implicit class toBitLengthFromSI(in: Double) {
    def Kilobyte = BitLength(in * 8 * 1000)
    def kB = Kilobyte

    def Megabyte = BitLength(in * 8 * 1000 * 1000)
    def MB = Megabyte

    def Gigabyte = BitLength(in * 8 * 1000 * 1000 * 1000)
    def GB = Gigabyte

    def Terabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000)
    def TB = Terabyte

    def Petabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000 * 1000)
    def PB = Petabyte

    def Exabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000)
    def EB = Exabyte

    def Zettabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000)
    def ZB = Zettabyte

    def Yottabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000)
    def YB = Yottabyte
  }
}
