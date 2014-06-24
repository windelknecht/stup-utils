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

import collection.mutable
import javafx.scene.image.Image

/**
 * Created by Me.
 * User: Heiko Blobner
 * Date: 11.09.12
 * Time: 14:48
 */

object ResourceImageLoader {
  // fields
  private var resourcePath = "."
  private val imageCache = new mutable.HashMap[String, Image]()

  // methods
  def isLoaded(path: String) = imageCache.contains(path)

  def getImage(path: String): Image = {
    if (!isLoaded(path))
      loadImage(path)

    imageCache(path)
  }

  def setBasePath(path: String) {
    resourcePath = if (path.endsWith("/")) path else "%s/".format(path)
  }

  // private methods
  private def loadImage(path: String) {
    imageCache += path -> new Image(ClassLoader.getSystemResourceAsStream(resourcePath + path))
  }
}
