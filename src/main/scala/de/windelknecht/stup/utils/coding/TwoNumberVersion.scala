package de.windelknecht.stup.utils.coding

import scala.language.implicitConversions

/**
 * Created by me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 31.07.13
 * Time: 21:5
 *
 *
 * Usage:
 *
 * import Version._
 *
 * val ver: Version = v1.3
 */
object TwoNumberVersion {
  private val fullVersion_r = """v(\d+)\.(\d+)$""".r
  private val majorVersion_r = """v(\d+)$""".r
  private val minorVersion_r = """v\.(\d+)$""".r

  implicit def toVersion(v: String): TwoNumberVersion = v match {
    case fullVersion_r (major,minor) => new TwoNumberVersion(major = major.toInt, minor = minor.toInt)
    case majorVersion_r(major      ) => new TwoNumberVersion(major = major.toInt                     )
    case minorVersion_r(      minor) => new TwoNumberVersion(                     minor = minor.toInt)

    case _                           => new TwoNumberVersion()
  }
}

case class TwoNumberVersion(
  major: Int = 0,
  minor: Int = 0
  ) extends Ordered[TwoNumberVersion] {
  /**
   * Result of comparing `this` with operand `that`.
   *
   * Implement this method to determine how instances of A will be sorted.
   *
   * Returns `x` where:
   *
   *   - `x < 0` when `this < that`
   *
   *   - `x == 0` when `this == that`
   *
   *   - `x > 0` when  `this > that`
   */
  override def compare(that: TwoNumberVersion) = major - that.major + minor - that.minor
}
