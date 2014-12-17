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
      test(DataLengthUnit.Zetabyte, DataLengthUnit.ZB)
      test(DataLengthUnit.Zebibyte, DataLengthUnit.ZiB)
    }

    "pass yotta byte" in {
      test(DataLengthUnit.Yottabyte, DataLengthUnit.YB)
      test(DataLengthUnit.Yobibyte, DataLengthUnit.YiB)
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
