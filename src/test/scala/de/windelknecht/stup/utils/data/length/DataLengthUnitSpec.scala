package de.windelknecht.stup.utils.data.length

import de.windelknecht.stup.utils.data.length.DataLengthUnit.DataLengthUnit
import org.scalatest.{Matchers, WordSpecLike}

class DataLengthUnitSpec
  extends WordSpecLike
  with Matchers {
  "checking enum definition" should {
    "pass bit" in {
      test(DataLengthUnit.bit, DataLengthUnit.bits)
    }

    "pass byte" in {
      test(DataLengthUnit.byte, DataLengthUnit.bytes)
      test(DataLengthUnit.byte, DataLengthUnit.B)
      test(DataLengthUnit.byte, DataLengthUnit.Byte)
      test(DataLengthUnit.byte, DataLengthUnit.Bytes)
    }

    "pass kilo byte" in {
      test(DataLengthUnit.Kilobyte, DataLengthUnit.kB)
      test(DataLengthUnit.Kibibyte, DataLengthUnit.KiB)
    }

    "pass mega byte" in {
      test(DataLengthUnit.Megabyte, DataLengthUnit.MB)
      test(DataLengthUnit.Mebibyte, DataLengthUnit.MiB)
    }

    "pass giga byte" in {
      test(DataLengthUnit.Gigabyte, DataLengthUnit.GB)
      test(DataLengthUnit.Gibibyte, DataLengthUnit.GiB)
    }

    "pass tera byte" in {
      test(DataLengthUnit.Terabyte, DataLengthUnit.TB)
      test(DataLengthUnit.Tebibyte, DataLengthUnit.TiB)
    }

    "pass peta byte" in {
      test(DataLengthUnit.Petabyte, DataLengthUnit.PB)
      test(DataLengthUnit.Pebibyte, DataLengthUnit.PiB)
    }

    "pass exa byte" in {
      test(DataLengthUnit.Exabyte, DataLengthUnit.EB)
      test(DataLengthUnit.Exbibyte, DataLengthUnit.EiB)
    }

    "pass zetta byte" in {
      test(DataLengthUnit.Zettabyte, DataLengthUnit.ZB)
      test(DataLengthUnit.Zebibyte, DataLengthUnit.ZiB)
    }

    "pass yotta byte" in {
      test(DataLengthUnit.Yottabyte, DataLengthUnit.YB)
      test(DataLengthUnit.Yobibyte, DataLengthUnit.YiB)
    }
  }

  "checking multiplier" when {
    "coming from 'bit'" should {
      "return correct on DataLengthUnit.bit" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.bit) should be (1) }
      "return correct on DataLengthUnit.bits" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.bits) should be (1) }
    }

    "coming from 'byte'" should {
      "return correct on DataLengthUnit.byte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.byte) should be (8) }
      "return correct on DataLengthUnit.bytes" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.bytes) should be (8) }
      "return correct on DataLengthUnit.B" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.B) should be (8) }
      "return correct on DataLengthUnit.Byte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Byte) should be (8) }
      "return correct on DataLengthUnit.Bytes" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Bytes) should be (8) }
    }

    "coming from 'kilo/kibi'" should {
      "return correct on DataLengthUnit.Kilobyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Kilobyte) should be (1000d * 8) }
      "return correct on DataLengthUnit.kB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.kB) should be (1000d * 8) }
      "return correct on DataLengthUnit.Kibibyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Kibibyte) should be (1024d * 8) }
      "return correct on DataLengthUnit.KiB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.KiB) should be (1024d * 8) }
    }

    "coming from 'mega/mebi'" should {
      "return correct on DataLengthUnit.Kilobyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Megabyte) should be (1000d * 1000 * 8) }
      "return correct on DataLengthUnit.kB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.MB) should be (1000d * 1000 * 8) }
      "return correct on DataLengthUnit.Kibibyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Mebibyte) should be (1024d * 1024 * 8) }
      "return correct on DataLengthUnit.KiB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.MiB) should be (1024d * 1024 * 8) }
    }

    "coming from 'giga/gibi'" should {
      "return correct on DataLengthUnit.Kilobyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Gigabyte) should be (1000d * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.kB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.GB) should be (1000d * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.Kibibyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Gibibyte) should be (1024d * 1024 * 1024 * 8) }
      "return correct on DataLengthUnit.KiB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.GiB) should be (1024d * 1024 * 1024 * 8) }
    }

    "coming from 'tera/tebi'" should {
      "return correct on DataLengthUnit.Kilobyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Terabyte) should be (1000d * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.kB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.TB) should be (1000d * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.Kibibyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Tebibyte) should be (1024d * 1024 * 1024 * 1024 * 8) }
      "return correct on DataLengthUnit.KiB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.TiB) should be (1024d * 1024 * 1024 * 1024 * 8) }
    }

    "coming from 'peta/pebi'" should {
      "return correct on DataLengthUnit.Kilobyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Petabyte) should be (1000d * 1000 * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.kB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.PB) should be (1000d * 1000 * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.Kibibyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Pebibyte) should be (1024d * 1024 * 1024 * 1024 * 1024 * 8) }
      "return correct on DataLengthUnit.KiB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.PiB) should be (1024d * 1024 * 1024 * 1024 * 1024 * 8) }
    }

    "coming from 'exa/exbi'" should {
      "return correct on DataLengthUnit.Kilobyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Exabyte) should be (1000d * 1000 * 1000 * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.kB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.EB) should be (1000d * 1000 * 1000 * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.Kibibyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Exbibyte) should be (1024d * 1024 * 1024 * 1024 * 1024 * 1024 * 8) }
      "return correct on DataLengthUnit.KiB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.EiB) should be (1024d * 1024 * 1024 * 1024 * 1024 * 1024 * 8) }
    }

    "coming from 'zetta/zebi'" should {
      "return correct on DataLengthUnit.Kilobyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Zettabyte) should be (1000d * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.kB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.ZB) should be (1000d * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.Kibibyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Zebibyte) should be (1024d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 8) }
      "return correct on DataLengthUnit.KiB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.ZiB) should be (1024d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 8) }
    }

    "coming from 'yotta/yobi'" should {
      "return correct on DataLengthUnit.Kilobyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Yottabyte) should be (1000d * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.kB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.YB) should be (1000d * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 8) }
      "return correct on DataLengthUnit.Kibibyte" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.Yobibyte) should be (1024d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 8) }
      "return correct on DataLengthUnit.KiB" in { DataLengthUnit.getMultiplierToBits(DataLengthUnit.YiB) should be (1024d * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 8) }
    }
  }

  def test(
    tto: DataLengthUnit,
    equal: DataLengthUnit*
    ): Unit = {
    DataLengthUnit.values.foreach { i =>
      if(equal.contains(i)) {
        withClue(s"$tto/${tto.id} should be $i/${i.id}, but") {
          tto should be(i)
        }
      } else {
        withClue(s"$tto/${tto.id} should not be $i/${i.id}, but") {
          tto should not be i
        }
      }
    }
  }
}
