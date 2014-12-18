package de.windelknecht.stup.utils.data

import de.windelknecht.stup.utils.data.length.ByteUnit.ByteUnit

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
    def byte = ByteLength(toBitLength(in).asByte, ByteUnit.B)
    def bytes = byte
    def B = byte
    def Byte = byte
    def Bytes = byte
  }

  implicit class toByteLengthFromIEC(in: Double) {
    def Kibibyte = create(toBitLengthFromIEC(in).asKiB, ByteUnit.KiB)
    def KiB = Kibibyte

    def Mebibyte = create(toBitLengthFromIEC(in).asMiB, ByteUnit.MiB)
    def MiB = Mebibyte

    def Gibibyte = create(toBitLengthFromIEC(in).asGiB, ByteUnit.GiB)
    def GiB = Gibibyte

    def Tebibyte = create(toBitLengthFromIEC(in).asTiB, ByteUnit.TiB)
    def TiB = Tebibyte

    def Pebibyte = create(toBitLengthFromIEC(in).asPiB, ByteUnit.PiB)
    def PiB = Pebibyte

    def Exbibyte = create(toBitLengthFromIEC(in).asEiB, ByteUnit.EiB)
    def EiB = Exbibyte

    def Zebibyte = create(toBitLengthFromIEC(in).asZiB, ByteUnit.ZiB)
    def ZiB = Zebibyte

    def Yobibyte = create(toBitLengthFromIEC(in).asYiB, ByteUnit.YiB)
    def YiB = Yobibyte

    private def create(bits: BitLength, unit: ByteUnit) = ByteLength(bits, unit)
  }

  implicit class toByteLengthFromSI(in: Double) {
    def Kilobyte = create(toBitLengthFromSI(in).asKB, ByteUnit.KB)
    def KB = Kilobyte

    def Megabyte = create(toBitLengthFromSI(in).asMB, ByteUnit.MB)
    def MB = Megabyte

    def Gigabyte = create(toBitLengthFromSI(in).asGB, ByteUnit.GB)
    def GB = Gigabyte

    def Terabyte = create(toBitLengthFromSI(in).asTB, ByteUnit.TB)
    def TB = Terabyte

    def Petabyte = create(toBitLengthFromSI(in).asPB, ByteUnit.PB)
    def PB = Petabyte

    def Exabyte = create(toBitLengthFromSI(in).asEB, ByteUnit.EB)
    def EB = Exabyte

    def Zettabyte = create(toBitLengthFromSI(in).asZB, ByteUnit.ZB)
    def ZB = Zettabyte

    def Yottabyte = create(toBitLengthFromSI(in).asYB, ByteUnit.YB)
    def YB = Yottabyte

    private def create(bits: BitLength, unit: ByteUnit) = ByteLength(bits, unit)
  }
}
