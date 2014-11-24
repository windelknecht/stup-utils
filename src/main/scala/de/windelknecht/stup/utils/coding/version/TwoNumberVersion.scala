package de.windelknecht.stup.utils.coding.version

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
  private val fullVersion_r = """(\d+)\.(\d+)$""".r
  private val majorVersion_r = """(\d+)$""".r
  private val minorVersion_r = """\.(\d+)$""".r

  /**
   * Convert from string to version.
   */
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
  )
  extends Version
  with HasMajorMinor
  with Ordered[Version]
  {
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
  override def compare(that: Version) = {
    that match {
      case v: PatchLevelVersion => if (compareMajorMinor(v) == 0) -v.patchLevel else compareMajorMinor(v)
      case v: TwoNumberVersion  => compareMajorMinor(v)

      case _ => 0
    }
  }

  override def toString = s"$major.$minor"
}
