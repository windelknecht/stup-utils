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

package de.windelknecht.stup.utils.ui.fxml

import java.io.IOException
import javafx.fxml.FXMLLoader
import javafx.scene.Node

object ResourceFxmlLoader {
  // fields
  private var resourcePath = "."

  // methods
  /*
  Load given fxml file up on base path.
   */
  def load[N <: Node](
    controller: AnyRef,
    file: String
    ): N = loadieModie[N](controller = Some(controller), filePath = "%s/%s".format(resourcePath, file)).getRoot.asInstanceOf[N]

  def load[N <: Node, C <: AnyRef](
    file: String
    ): (N,C) = {
    val f = loadieModie[N](filePath = "%s/%s".format(resourcePath, file))

    (f.getRoot.asInstanceOf[N], f.getController.asInstanceOf[C])
  }

  /*
  Load fxml file from given path - and not up on base path.
   */
  def loadFrom[N <: Node](
    controller: AnyRef,
    filePath: String
    ): N = loadieModie[N](controller = Some(controller), filePath = filePath).getRoot.asInstanceOf[N]

  def loadFrom[N <: Node, C <: AnyRef](
    filePath: String
    ): (N,C) = {
    val f = loadieModie[N](filePath = filePath)

    (f.getRoot.asInstanceOf[N], f.getController.asInstanceOf[C])
  }

  def setBasePath(path: String) {
    resourcePath = if (path.endsWith("/")) path else path
  }

  private def loadieModie[N <: Node](
    controller: Option[AnyRef] = None,
    filePath: String
    ): FXMLLoader = {
    val fxmlLoader = new FXMLLoader(
      ClassLoader
        .getSystemResource(filePath)
        .ensuring(_ != null, filePath)
    )
    controller match {
      case Some(x) => fxmlLoader.setController(x)
      case None =>
    }
    try {
      fxmlLoader.load()
    } catch {
      case e: IOException =>
        throw new RuntimeException(e)
    }

    fxmlLoader
  }
}
