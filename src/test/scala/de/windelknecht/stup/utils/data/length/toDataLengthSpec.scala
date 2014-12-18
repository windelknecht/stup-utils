package de.windelknecht.stup.utils.data.length

import de.windelknecht.stup.utils.data.length.DataLengthUnit.DataLengthUnit
import org.scalatest.{Matchers, WordSpecLike}

class toDataLengthSpec
  extends WordSpecLike
  with Matchers {
  "passing bit unit" when {
    "using function 'bit'" should {
      test(new toDataLength(8).bit, 8, DataLengthUnit.bit)
    }

    "using function 'bits'" should {
      test(new toDataLength(8).bits, 8, DataLengthUnit.bits)
    }
  }

  "passing byte unit" when {
    "using function 'byte'" should {
      test(new toDataLength(8).byte, 8, DataLengthUnit.byte)
    }

    "using function 'bytes'" should {
      test(new toDataLength(8).bytes, 8, DataLengthUnit.bytes)
    }

    "using function 'B'" should {
      test(new toDataLength(8).B, 8, DataLengthUnit.B)
    }

    "using function 'Byte'" should {
      test(new toDataLength(8).Byte, 8, DataLengthUnit.Byte)
    }

    "using function 'Bytes'" should {
      test(new toDataLength(8).Bytes, 8, DataLengthUnit.Bytes)
    }
  }

  "passing kilo/kibi-byte unit" when {
    "using function 'Kilobyte'" should {
      test(new toDataLength(8).Kilobyte, 8, DataLengthUnit.Kilobyte)
    }

    "using function 'kB'" should {
      test(new toDataLength(8).kB, 8, DataLengthUnit.kB)
    }

    "using function 'Kibibyte'" should {
      test(new toDataLength(8).Kibibyte, 8, DataLengthUnit.Kibibyte)
    }

    "using function 'KiB'" should {
      test(new toDataLength(8).KiB, 8, DataLengthUnit.KiB)
    }
  }

  "passing mega/mebi-byte unit" when {
    "using function 'Megabyte'" should {
      test(new toDataLength(8).Megabyte, 8, DataLengthUnit.Megabyte)
    }

    "using function 'MB'" should {
      test(new toDataLength(8).MB, 8, DataLengthUnit.MB)
    }

    "using function 'Mebibyte'" should {
      test(new toDataLength(8).Mebibyte, 8, DataLengthUnit.Mebibyte)
    }

    "using function 'MiB'" should {
      test(new toDataLength(8).MiB, 8, DataLengthUnit.MiB)
    }
  }

  "passing giga/gibi-byte unit" when {
    "using function 'Gigabyte'" should {
      test(new toDataLength(8).Gigabyte, 8, DataLengthUnit.Gigabyte)
    }

    "using function 'GB'" should {
      test(new toDataLength(8).GB, 8, DataLengthUnit.GB)
    }

    "using function 'Gibibyte'" should {
      test(new toDataLength(8).Gibibyte, 8, DataLengthUnit.Gibibyte)
    }

    "using function 'GiB'" should {
      test(new toDataLength(8).GiB, 8, DataLengthUnit.GiB)
    }
  }

  "passing tera/tebi-byte unit" when {
    "using function 'Terabyte'" should {
      test(new toDataLength(8).Terabyte, 8, DataLengthUnit.Terabyte)
    }

    "using function 'TB'" should {
      test(new toDataLength(8).TB, 8, DataLengthUnit.TB)
    }

    "using function 'Tebibyte'" should {
      test(new toDataLength(8).Tebibyte, 8, DataLengthUnit.Tebibyte)
    }

    "using function 'TiB'" should {
      test(new toDataLength(8).TiB, 8, DataLengthUnit.TiB)
    }
  }

  "passing peta/pebi-byte unit" when {
    "using function 'Petabyte'" should {
      test(new toDataLength(8).Petabyte, 8, DataLengthUnit.Petabyte)
    }

    "using function 'PB'" should {
      test(new toDataLength(8).PB, 8, DataLengthUnit.PB)
    }

    "using function 'Pebibyte'" should {
      test(new toDataLength(8).Pebibyte, 8, DataLengthUnit.Pebibyte)
    }

    "using function 'PiB'" should {
      test(new toDataLength(8).PiB, 8, DataLengthUnit.PiB)
    }
  }

  "passing exa/exbi-byte unit" when {
    "using function 'Exabyte'" should {
      test(new toDataLength(8).Exabyte, 8, DataLengthUnit.Exabyte)
    }

    "using function 'EB'" should {
      test(new toDataLength(8).EB, 8, DataLengthUnit.EB)
    }

    "using function 'Exbibyte'" should {
      test(new toDataLength(8).Exbibyte, 8, DataLengthUnit.Exbibyte)
    }

    "using function 'EiB'" should {
      test(new toDataLength(8).EiB, 8, DataLengthUnit.EiB)
    }
  }

  "passing zetta/zebi-byte unit" when {
    "using function 'Zettabyte'" should {
      test(new toDataLength(8).Zettabyte, 8, DataLengthUnit.Zettabyte)
    }

    "using function 'ZB'" should {
      test(new toDataLength(8).ZB, 8, DataLengthUnit.ZB)
    }

    "using function 'Zebibyte'" should {
      test(new toDataLength(8).Zebibyte, 8, DataLengthUnit.Zebibyte)
    }

    "using function 'ZiB'" should {
      test(new toDataLength(8).ZiB, 8, DataLengthUnit.ZiB)
    }
  }

  "passing yotta/yobi-byte unit" when {
    "using function 'Yotabyte'" should {
      test(new toDataLength(8).Yottabyte, 8, DataLengthUnit.Yottabyte)
    }

    "using function 'YB'" should {
      test(new toDataLength(8).YB, 8, DataLengthUnit.YB)
    }

    "using function 'Yobibyte'" should {
      test(new toDataLength(8).Yobibyte, 8, DataLengthUnit.Yobibyte)
    }

    "using function 'YiB'" should {
      test(new toDataLength(8).YiB, 8, DataLengthUnit.YiB)
    }
  }

  def test(
    data: DataLength,
    expectedLen: Int,
    expectedUnit: DataLengthUnit
    ): Unit = {
    s"return correct len" in {
      withClue(s"$data.len should be $expectedLen, but") {
        data.len should be(expectedLen)
      }
    }

    s"return correct unit" in {
      withClue(s"$data.unit should be $expectedUnit, but") {
        data.unit should be(expectedUnit)
      }
    }
  }
}
