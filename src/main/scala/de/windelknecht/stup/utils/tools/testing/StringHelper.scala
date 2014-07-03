package de.windelknecht.stup.utils.tools.testing

import de.windelknecht.stup.utils.tools.RandomHelper

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 01.11.13
 * Time: 14:35
 *
 */
object StringHelper {
  /**
   * Append string with some noise.
   */
  def appendWithNoise(content: String) = new StringBuilder()
    .append(content)
    .append(RandomHelper.rndString(1024))
    .result()

  /**
   * Prepend string with some noise.
   */
  def prependWithNoise(content: String) = new StringBuilder()
    .append(RandomHelper.rndString(1024))
    .append(content)
    .result()

  /**
   * Surround a string with random noise strings.
   */
  def surroundWithNoise(content: String) = new StringBuilder()
    .append(RandomHelper.rndString(1024))
    .append(content)
    .append(RandomHelper.rndString(1024))
    .result()
}
