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

package de.windelknecht.stup.utils.ui

/**
 * This trait is the base for all custom resource loader.
 *
 * @author Heiko Blobner <windelknecht@gmail.com>
 * @version %I%, %G%
 * @since 0.1
 */
trait BaseResourceLoader {
  // fields
  private var _resourcePath = "."

  /**
   * Build absolute path from base path and given filename.
   *
   * @param fileName is the relative resource filename
   * @return absolute resource filename
   */
  def buildPath(
    fileName: String
    ) = s"${_resourcePath}/$fileName"

  /**
   * Return set base resource path.
   *
   * @return s.o.
   */
  def resourcePath = _resourcePath

  /**
   * Set base resource path.
   * All loading instructions can be relative from now.
   *
   * @param path is the base path
   */
  def setResourcePath(
    path: String
    ) {
    _resourcePath = path
  }
}
