package de.windelknecht.stup.utils.coding.version

trait Version

trait HasMajorMinor {
  def major: Int
  def minor: Int

  def compareMajorMinor(that: HasMajorMinor) = major - that.major + minor - that.minor
}
