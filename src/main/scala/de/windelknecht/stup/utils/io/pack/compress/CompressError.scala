package de.windelknecht.stup.utils.io.pack.compress

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 06.01.14
 * Time: 10:41
 *
 */
object CompressError
  extends Enumeration {
  type CompressError = Value

  val UnsupportedCompressor = Value
  val SrcFileDoesntExist = Value
}
