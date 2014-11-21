/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Heiko Blobner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.windelknecht.stup.utils.coding.plugin

import de.windelknecht.stup.utils.coding.version.PatchLevelVersion

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 23.07.14
 * Time: 13:33
 *
 */
trait PluginDescr {
  /**
   * Defines the version of the main application the plugin is compiled against.
   * The app can now decide if this plugin should work or not (maybe api changes)
   *
   * @return the version information
   */
  def compiledAgainst: PatchLevelVersion

  /**
   * Plugin description
   *
   * @return plugin description
   */
  def description: String

  /**
   * Plugin name.
   *
   * @return the name of this plugin
   */
  def name: String

  /**
   * Plugin author.
   *
   * @return author name
   */
  def author: String

  /**
   * Author email address..
   *
   * @return author email address
   */
  def email: String

  /**
   * Plugin version.
   *
   * @return version of this plugin
   */
  def version: PatchLevelVersion
}
