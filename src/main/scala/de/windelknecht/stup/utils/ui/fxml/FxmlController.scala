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

import de.windelknecht.stup.utils.coding.Implicits._
import de.windelknecht.stup.utils.coding.Stoppable
import de.windelknecht.stup.utils.coding.reactive.Notify
import de.windelknecht.stup.utils.coding.reactive.Notify.NotifyEvent
import de.windelknecht.stup.utils.ui.fxml.FxmlController._
import java.net.URL
import java.util.{UUID, ResourceBundle}
import javafx.application.Platform
import javafx.fxml.Initializable
import javafx.scene.Node

object FxmlController {
  trait FxmlControllerEvent   extends NotifyEvent
  case object OnGoingActive   extends FxmlControllerEvent
  case object OnIsActive      extends FxmlControllerEvent
  case object OnGoingInactive extends FxmlControllerEvent
  case object OnIsInactive    extends FxmlControllerEvent
  case object OnShutdown      extends FxmlControllerEvent
}

trait FxmlController
  extends Initializable
  with Notify
  with Stoppable {
  // fields
  protected val _uuid = UUID.randomUUID()

  /**
   * Return this screen ui node.
   */
  def baseUINode: Node

  /**
   * Does initialization.
   *
   * @param url url det Dingens
   * @param bundle bundle det Dingens
   */
  def initialize(url: URL, bundle: ResourceBundle) {}

  /**
   * Get called, when this screen is activated.
   */
  def youAreGoingActive() { fireNotify(OnGoingActive, Nil) }

  /**
   * Get called, when this screen is activate.
   */
  def youAreActive() { fireNotify(OnIsActive, Nil) }

  /**
   * Get called, when this screen is deactivated.
   */
  def youAreGoingInactive() { fireNotify(OnGoingInactive, Nil) }

  /**
   * Get called, when this screen is inactivate.
   */
  def youAreInactive() { fireNotify(OnIsInactive, Nil) }

  /**
   * Shutdown
   */
  def shutdown() { fireNotify(OnShutdown, Nil) }

  /**
   * This method must be called on asynchron access on ui elements.
   *
   * @param fun function to execute within ui context
   */
  protected def doUI(fun: => Unit) {
    Platform.runLater(fun)
  }
}
