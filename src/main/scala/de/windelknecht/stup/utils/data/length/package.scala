package de.windelknecht.stup.utils.data

package object length {
  implicit class toBitLength(in: Double) {
    def bit = BitLength(in)
    def bits = bit
    def Bit = bit
    def Bits = bit

    def asByte = BitLength(in * 8)
  }

  implicit class toBitLengthFromIEC(in: Double) {
    def asKibibyte = BitLength(in * 8 * 1024)
    def asKiB = asKibibyte

    def asMebibyte = BitLength(in * 8 * 1024 * 1024)
    def asMiB = asMebibyte

    def asGibibyte = BitLength(in * 8 * 1024 * 1024 * 1024)
    def asGiB = asGibibyte

    def asTebibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024)
    def asTiB = asTebibyte

    def asPebibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024 * 1024)
    def asPiB = asPebibyte

    def asExbibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024)
    def asEiB = asExbibyte

    def asZebibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024)
    def asZiB = asZebibyte

    def asYobibyte = BitLength(in * 8 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024)
    def asYiB = asYobibyte
  }

  implicit class toBitLengthFromSI(in: Double) {
    def asKilobyte = BitLength(in * 8 * 1000)
    def asKB = asKilobyte

    def asMegabyte = BitLength(in * 8 * 1000 * 1000)
    def asMB = asMegabyte

    def asGigabyte = BitLength(in * 8 * 1000 * 1000 * 1000)
    def asGB = asGigabyte

    def asTerabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000)
    def asTB = asTerabyte

    def asPetabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000 * 1000)
    def asPB = asPetabyte

    def asExabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000)
    def asEB = asExabyte

    def asZettabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000)
    def asZB = asZettabyte

    def asYottabyte = BitLength(in * 8 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000)
    def asYB = asYottabyte
  }

  implicit class toByteLength(in: Double) {
    def byte = ByteLength(toBitLength(in).asByte)
    def bytes = byte
    def B = byte
    def Byte = byte
    def Bytes = byte
  }

  implicit class toByteLengthFromIEC(in: Double) {
    def Kibibyte = create(toBitLengthFromIEC(in).asKiB)
    def KiB = Kibibyte

    def Mebibyte = create(toBitLengthFromIEC(in).asMiB)
    def MiB = Mebibyte

    def Gibibyte = create(toBitLengthFromIEC(in).asGiB)
    def GiB = Gibibyte

    def Tebibyte = create(toBitLengthFromIEC(in).asTiB)
    def TiB = Tebibyte

    def Pebibyte = create(toBitLengthFromIEC(in).asPiB)
    def PiB = Pebibyte

    def Exbibyte = create(toBitLengthFromIEC(in).asEiB)
    def EiB = Exbibyte

    def Zebibyte = create(toBitLengthFromIEC(in).asZiB)
    def ZiB = Zebibyte

    def Yobibyte = create(toBitLengthFromIEC(in).asYiB)
    def YiB = Yobibyte

    private def create(bits: BitLength) = ByteLength(bits)
  }

  implicit class toByteLengthFromSI(in: Double) {
    def Kilobyte = create(toBitLengthFromSI(in).asKB)
    def KB = Kilobyte

    def Megabyte = create(toBitLengthFromSI(in).asMB)
    def MB = Megabyte

    def Gigabyte = create(toBitLengthFromSI(in).asGB)
    def GB = Gigabyte

    def Terabyte = create(toBitLengthFromSI(in).asTB)
    def TB = Terabyte

    def Petabyte = create(toBitLengthFromSI(in).asPB)
    def PB = Petabyte

    def Exabyte = create(toBitLengthFromSI(in).asEB)
    def EB = Exabyte

    def Zettabyte = create(toBitLengthFromSI(in).asZB)
    def ZB = Zettabyte

    def Yottabyte = create(toBitLengthFromSI(in).asYB)
    def YB = Yottabyte

    private def create(bits: BitLength) = ByteLength(bits)
  }
}
