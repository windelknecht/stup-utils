package de.windelknecht.stup.utils.data.length

import org.scalatest.{Matchers, WordSpecLike}

class DataLengthSpec
  extends WordSpecLike
  with Matchers {
  "do a conversion" when {
    "returning as bits" should {
      "from bit" in { DataLength(7, DataLengthUnit.bit).asBits should be (7) }
      "from byte" in { DataLength(7, DataLengthUnit.Byte).asBits should be (56) }
    }

    "returning as bytes" should {
      "from bit (aligned)" in { DataLength(16, DataLengthUnit.bit).asB should be (2) }
      "from bit (unaligned, close to lower)" in { DataLength(1, DataLengthUnit.bit).asB should be (1) }
      "from bit (unaligned, close to upper)" in { DataLength(7, DataLengthUnit.bit).asB should be (1) }
      "from kB" in { DataLength(1, DataLengthUnit.kB).asB should be (1000) }
      "from KiB" in { DataLength(1, DataLengthUnit.KiB).asB should be (1024) }
    }

    "returning as kB" should {
      "from byte (aligned)" in { DataLength(2000, DataLengthUnit.byte).asKB should be (2) }
      "from byte (unaligned, close to lower)" in { DataLength(1099, DataLengthUnit.byte).asKB should be (2) }
      "from byte (unaligned, close to upper)" in { DataLength(1990, DataLengthUnit.byte).asKB should be (2) }
    }

    "returning as KiB" should {
      "from byte (aligned)" in { DataLength(2000, DataLengthUnit.byte).asKiB should be (2) }
      "from byte (unaligned, close to lower)" in { DataLength(1025, DataLengthUnit.byte).asKiB should be (2) }
      "from byte (unaligned, close to upper)" in { DataLength(1023, DataLengthUnit.byte).asKiB should be (1) }
      "from kB" in { DataLength(56, DataLengthUnit.kB).asKiB should be (55) }
    }

    "returning as MB" should {
      "from kB (aligned)" in { DataLength(2000, DataLengthUnit.kB).asMB should be (2) }
      "from kB (unaligned, close to lower)" in { DataLength(1099, DataLengthUnit.kB).asMB should be (2) }
      "from kB (unaligned, close to upper)" in { DataLength(1990, DataLengthUnit.kB).asMB should be (2) }
    }

    "returning as MiB" should {
      "from KiB (aligned)" in { DataLength(2000, DataLengthUnit.KiB).asMiB should be (2) }
      "from KiB (unaligned, close to lower)" in { DataLength(1025, DataLengthUnit.KiB).asMiB should be (2) }
      "from KiB (unaligned, close to upper)" in { DataLength(1023, DataLengthUnit.KiB).asMiB should be (1) }
    }

    "returning as GB" should {
      "from MB (aligned)" in { DataLength(2000, DataLengthUnit.MB).asGB should be (2) }
      "from MB (unaligned, close to lower)" in { DataLength(1099, DataLengthUnit.MB).asGB should be (2) }
      "from MB (unaligned, close to upper)" in { DataLength(1990, DataLengthUnit.MB).asGB should be (2) }
    }

    "returning as GiB" should {
      "from MiB (aligned)" in { DataLength(2000, DataLengthUnit.MiB).asGiB should be (2) }
      "from MiB (unaligned, close to lower)" in { DataLength(1025, DataLengthUnit.MiB).asGiB should be (2) }
      "from MiB (unaligned, close to upper)" in { DataLength(1023, DataLengthUnit.MiB).asGiB should be (1) }
    }

    "returning as TB" should {
      "from GB (aligned)" in { DataLength(2000, DataLengthUnit.GB).asTB should be (2) }
      "from GB (unaligned, close to lower)" in { DataLength(1099, DataLengthUnit.GB).asTB should be (2) }
      "from GB (unaligned, close to upper)" in { DataLength(1990, DataLengthUnit.GB).asTB should be (2) }
    }

    "returning as TiB" should {
      "from GiB (aligned)" in { DataLength(2000, DataLengthUnit.GiB).asTiB should be (2) }
      "from GiB (unaligned, close to lower)" in { DataLength(1025, DataLengthUnit.GiB).asTiB should be (2) }
      "from GiB (unaligned, close to upper)" in { DataLength(1023, DataLengthUnit.GiB).asTiB should be (1) }
    }

    "returning as PB" should {
      "from TB (aligned)" in { DataLength(2000, DataLengthUnit.TB).asPB should be (2) }
      "from TB (unaligned, close to lower)" in { DataLength(1099, DataLengthUnit.TB).asPB should be (2) }
      "from TB (unaligned, close to upper)" in { DataLength(1990, DataLengthUnit.TB).asPB should be (2) }
    }

    "returning as PiB" should {
      "from TiB (aligned)" in { DataLength(2000, DataLengthUnit.TiB).asPiB should be (2) }
      "from TiB (unaligned, close to lower)" in { DataLength(1025, DataLengthUnit.TiB).asPiB should be (2) }
      "from TiB (unaligned, close to upper)" in { DataLength(1023, DataLengthUnit.TiB).asPiB should be (1) }
    }

    "returning as EB" should {
      "from PB (aligned)" in { DataLength(2000, DataLengthUnit.PB).asEB should be (2) }
      "from PB (unaligned, close to lower)" in { DataLength(1099, DataLengthUnit.PB).asEB should be (2) }
      "from PB (unaligned, close to upper)" in { DataLength(1990, DataLengthUnit.PB).asEB should be (2) }
    }

    "returning as EiB" should {
      "from PiB (aligned)" in { DataLength(2000, DataLengthUnit.PiB).asEiB should be (2) }
      "from PiB (unaligned, close to lower)" in { DataLength(1025, DataLengthUnit.PiB).asEiB should be (2) }
      "from PiB (unaligned, close to upper)" in { DataLength(1023, DataLengthUnit.PiB).asEiB should be (1) }
    }

    "returning as ZB" should {
      "from EB (aligned)" in { DataLength(2000, DataLengthUnit.EB).asZB should be (2) }
      "from EB (unaligned, close to lower)" in { DataLength(1099, DataLengthUnit.EB).asZB should be (2) }
      "from EB (unaligned, close to upper)" in { DataLength(1990, DataLengthUnit.EB).asZB should be (2) }
    }

    "returning as ZiB" should {
      "from EiB (aligned)" in { DataLength(2000, DataLengthUnit.EiB).asZiB should be (2) }
      "from EiB (unaligned, close to lower)" in { DataLength(1025, DataLengthUnit.EiB).asZiB should be (2) }
      "from EiB (unaligned, close to upper)" in { DataLength(1023, DataLengthUnit.EiB).asZiB should be (1) }
    }

    "returning as YB" should {
      "from ZB (aligned)" in { DataLength(2000, DataLengthUnit.ZB).asYB should be (2) }
      "from ZB (unaligned, close to lower)" in { DataLength(1099, DataLengthUnit.ZB).asYB should be (2) }
      "from ZB (unaligned, close to upper)" in { DataLength(1990, DataLengthUnit.ZB).asYB should be (2) }
    }

    "returning as YiB" should {
      "from ZiB (aligned)" in { DataLength(2000, DataLengthUnit.ZiB).asYiB should be (2) }
      "from ZiB (unaligned, close to lower)" in { DataLength(1025, DataLengthUnit.ZiB).asYiB should be (2) }
      "from ZiB (unaligned, close to upper)" in { DataLength(1023, DataLengthUnit.ZiB).asYiB should be (1) }
    }
  }
}
