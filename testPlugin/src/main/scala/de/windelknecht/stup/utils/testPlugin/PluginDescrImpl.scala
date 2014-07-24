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

package de.windelknecht.stup.utils.testPlugin

import de.windelknecht.stup.utils.coding.Version
import de.windelknecht.stup.utils.coding.plugin.PluginDescr

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 23.07.14
 * Time: 13:51
 *
 */
class PluginDescrImpl
  extends PluginDescr {
  /**
   * Defines the version of the main application the plugin is compiled against.
   * The app can now decide if this plugin should work or not (maybe api changes)
   *
   * @return the version information
   */
  override def compiledAgainst = Version(0, 0, 1)

  /**
   * Plugin description
   *
   * @return plugin description
   */
  override def description = "descr"

  /**
   * Plugin name.
   *
   * @return the name of this plugin
   */
  override def name = "name"

  /**
   * Plugin author.
   *
   * @return author name
   */
  override def author = "author"

  /**
   * Author email address..
   *
   * @return author email address
   */
  override def email = "email"

  /**
   * Plugin version.
   *
   * @return version of this plugin
   */
  override def version = Version(0, 0, 1)
}
