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

import de.windelknecht.stup.utils.ui.BaseResourceLoader
import java.io.{ByteArrayInputStream, File}
import javafx.fxml.FXMLLoader
import javafx.scene.Node

/**
 * This object loads fxml files via fxml loader and return controller and view node..
 * Don't forget to set the base path. Otherwise all fxml files
 * will be prefixed with '.'.
 *
 * @author Heiko Blobner <windelknecht@gmail.com>
 * @version %I%, %G%
 * @since 0.1
 */
object ResourceFxmlLoader
  extends BaseResourceLoader {
  /**
   * This method loads a fxml file and pass the given controller object.
   *
   * @param fileName is the filename to the fxml file
   * @param controller is the controller object for to new created ui fxml node
   * @param isAResource if true, the image is loaded from internal resource path
   * @param isRelativePath is the filename relative or absolute
   * @tparam N is the type parameter from the loaded ui node
   * @tparam C is the type parameter from the controller
   * @return the loaded ui node and the (loaded or passed) controller
   *
   * @throws IOException if there is an I/O error
   */
  def loadFile[N <: Node, C <: AnyRef](
    fileName: String,
    controller: Option[C] = None,
    isAResource: Boolean = true,
    isRelativePath: Boolean = true
    ): (N, C) = {
    trace(s"loading file $fileName (controller=$controller, isAResource=$isAResource, isRelativePath=$isRelativePath)")

    if(isAResource)
      loadFromResource[N,C](controller = controller, filePath = fileName, isRelativePath = isRelativePath)
    else
      loadFromFile[N,C](controller = controller, filePath = fileName, isRelativePath = isRelativePath)
  }

  /**
   * This method loads a fxml string and pass the given controller object.
   *
   * @param fxml is the fxml content
   * @param controller is the controller object for to new created ui fxml node
   * @tparam N is the type parameter from the loaded ui node
   * @tparam C is the type parameter from the controller
   * @return the loaded ui node and the (loaded or passed) controller
   */
  def loadString[N <: Node, C <: AnyRef](
    fxml: String,
    controller: Option[C] = None
    ): (N, C) = {
    trace(s"loading fxml string (controller=$controller)")

    val fxmlLoader = new FXMLLoader()

    controller match {
      case Some(x) => fxmlLoader.setController(x)
      case None =>
    }
    fxmlLoader.load(new ByteArrayInputStream(fxml.getBytes))

    (fxmlLoader.getRoot.asInstanceOf[N], fxmlLoader.getController.asInstanceOf[C])
  }

  /**
   * This method loads a local fxml file and returns the ui node and the controller object.
   *
   * @param controller is an eventually existing controller object
   * @param filePath is the filename of the fxml file
   * @param isRelativePath says if the filename is relative or absolute
   * @tparam N is the type of the ui node
   * @tparam C is the type of the controller
   * @return ui node and (loaded or passed) controller
   *
   * @throws IOException if there is an I/O error
   */
  private def loadFromFile[N <: Node, C <: AnyRef](
    controller: Option[C] = None,
    filePath: String,
    isRelativePath: Boolean = true
    ): (N, C) = loadFromURL(controller, new File(if(isRelativePath) buildPath(filePath) else filePath).toURI.toURL)

  /**
   * This method loads a fxml resource file and returns the ui node and the controller object.
   *
   * @param controller is an eventually existing controller object
   * @param filePath is the filename of the fxml file
   * @param isRelativePath says if the filename is relative or absolute
   * @tparam N is the type of the ui node
   * @tparam C is the type of the controller
   * @return ui node and (loaded or passed) controller
   *
   * @throws IOException if there is an I/O error
   */
  private def loadFromResource[N <: Node, C <: AnyRef](
    controller: Option[C] = None,
    filePath: String,
    isRelativePath: Boolean = true
    ): (N, C) = loadFromURL(controller, ClassLoader.getSystemResource(if(isRelativePath) buildPath(filePath) else filePath))

  /**
   * This method loads a fxml file from given url and returns the ui node and the controller object.
   *
   * @param controller is an eventually existing controller object
   * @param fileURL file url location
   * @tparam N is the type of the ui node
   * @tparam C is the type of the controller
   * @return ui node and (loaded or passed) controller
   *
   * @throws IOException if there is an I/O error
   */
  private def loadFromURL[N <: Node, C <: AnyRef](
    controller: Option[C] = None,
    fileURL: java.net.URL
    ): (N, C) = {
    val fxmlLoader = new FXMLLoader(fileURL)

    controller match {
      case Some(x) => fxmlLoader.setController(x)
      case None =>
    }
    fxmlLoader.load()

    (fxmlLoader.getRoot.asInstanceOf[N], fxmlLoader.getController.asInstanceOf[C])
  }
}
