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

import _root_.javafx.scene.image.Image

import collection.mutable

/**
 * This object loads images and caches them for faster access.
 * Don't forget to set the base path. Otherwise all image files
 * will be prefixed with '.'.
 *
 * @author Heiko Blobner <windelknecht@gmail.com>
 * @version %I%, %G%
 * @since 0.1
 */
object ResourceImageLoader
  extends BaseResourceLoader {
  // fields
  private val _cache = new mutable.HashMap[String, Image]()

  /**
   * Put given image into cache
   *
   * @param ident is the ident key
   * @param image is the image itself
   * @return the passed image
   */
  def cacheImage(
    ident: String,
    image: Image
    ): Image = {
    _cache += (ident -> image)
    image
  }

  /**
   * Returns true if the given image file is already cached.
   * 
   * @param ident is the image ident key
   * @return true -> already cached, false -> not cached
   */
  def isLoaded(
    ident: String
    ) = _cache.contains(ident)

  /**
   * Load an image or return an already loaded and now cached image.
   *
   * @param fileName path to the image file
   * @param isAResource if true, the image is loaded from internal resource path
   * @param isRelativePath should be false. if the filename path os absolute
   * @return loaded image
   *
   * @throws IOException if an I/O exception occurs.
   */
  def get(
    fileName: String,
    isAResource: Boolean = true,
    isRelativePath: Boolean = true
    ) = _cache.getOrElseUpdate(fileName, if(isAResource) loadResource(fileName, isRelativePath) else loadFile(fileName, isRelativePath))

  /**
   * Load image from given path.
   *
   * @param fileName is the filename of the image
   * @param isRelativePath should be false. if the filename path os absolute
   * @return loaded image
   *
   * @throws IOException if an I/O exception occurs.
   */
  private def loadFile(
    fileName: String,
    isRelativePath: Boolean
    ) = {
    trace(s"load file: '$fileName' (isRelativePath=$isRelativePath)")

    new Image(if(isRelativePath) buildPath(fileName) else fileName)
  }

  /**
   * Load image from internal resource stream.
   *
   * @param fileName is the filename of the image
   * @param isRelativePath should be false. if the filename path os absolute
   * @return loaded image
   *
   * @throws IOException if an I/O exception occurs.
   */
  private def loadResource(
    fileName: String,
    isRelativePath: Boolean
    ) = {
    trace(s"load resource: '$fileName' (isRelativePath=$isRelativePath)")

    new Image(ClassLoader.getSystemResourceAsStream(if(isRelativePath) buildPath(fileName) else fileName))
  }
}
