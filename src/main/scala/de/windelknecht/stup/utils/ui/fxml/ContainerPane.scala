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
import de.windelknecht.stup.utils.coding.reactive.ErrorReporter.OnError
import de.windelknecht.stup.utils.coding.reactive.{ErrorReporter, Notify}
import de.windelknecht.stup.utils.coding.reactive.Notify.NotifyEvent
import de.windelknecht.stup.utils.ui.fxml.ContainerPane._
import javafx.animation.{Animation, KeyFrame, KeyValue, Timeline}
import javafx.application.Platform
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Node
import javafx.scene.layout.AnchorPane
import javafx.{util => jxu}
import scala.collection.mutable

object ContainerPane {
  sealed trait ContentPaneEvent  extends NotifyEvent
  case object  OnContentActive   extends ContentPaneEvent
  case object  OnContentInactive extends ContentPaneEvent
}

sealed trait ContainerPaneError
case class ContentKeyNotRegistered[T](key: T) extends ContainerPaneError

trait ContainerPane[K]
  extends ErrorReporter { this: Notify =>
  case class ContentDescr(ui: FxmlController, fadeIn: Animation, fadeOut: Animation)

  // fields
  protected val DEFAULT_FADING_SPEED: Double = 200d

  protected var _containerNode: AnchorPane
  private val _registeredContent = new mutable.HashMap[K, ContentDescr]()

  /**
   * This is the default fade in animation.
   */
  private lazy val _fadeIn = new Timeline(
    new KeyFrame(    jxu.Duration.ZERO,                  new KeyValue(_containerNode.opacityProperty(), double2Double(0.0))),
    new KeyFrame(new jxu.Duration(DEFAULT_FADING_SPEED), new KeyValue(_containerNode.opacityProperty(), double2Double(1.0)))
  )

  /**
   * This is the default fade out animation.
   */
  private lazy val _fadeOut = new Timeline(
    new KeyFrame(    jxu.Duration.ZERO,                  new KeyValue(_containerNode.opacityProperty(), double2Double(1.0))),
    new KeyFrame(new jxu.Duration(DEFAULT_FADING_SPEED), new KeyValue(_containerNode.opacityProperty(), double2Double(0.0)))
  )

  /**
   * Clear all registered contents.
   */
  def clearContent() {
    _registeredContent.synchronized {
      _registeredContent.clear()
    }
  }

  /**
   * Load content ui with given contentKey.
   *
   * @param key is the key identifying the ui
   * @param ui is the ui node to show or hide
   * @param fadeIn is the animation which is played on activation
   * @param fadeOut is the animation which is played on deactivation
   */
  def loadContent(
    key: K,
    ui: FxmlController,
    fadeIn: Animation = _fadeIn,
    fadeOut: Animation = _fadeOut
    ) {
    _registeredContent.synchronized {
      _registeredContent += (key -> ContentDescr(ui, fadeIn = fadeIn, fadeOut = fadeOut))
    }

    if(_containerNode.getChildren.isEmpty)
      switchToContent(key)
  }

  /**
   * unload given content.
   *
   * TODO: remove ui
   */
  def unloadContent(
    key: K
    ) {
    _registeredContent.synchronized {
      _registeredContent -= key
    }
  }

  /**
   * Shutdown this ...
   */
  def shutdown() {
    Platform.runLater {
      _containerNode.setOpacity(0)
      _containerNode.getChildren.clear()
    }

    _registeredContent.synchronized {
      _registeredContent.clear()
    }
  }

  /**
   * Make content with this key active and visible.
   *
   * @param key is the key assigned to the content ui
   */
  def switchToContent(
    key: K
    ) {
    _registeredContent.get(key) match {
      case Some(x) => switchTo(key, x)
      case None    => reportWarningErr(ContentKeyNotRegistered(key))
    }
  }

  /**
   * Prepare the ui to be faded in.
   * (This method must run within the ui context thread)
   *
   * 1. remove old content
   * 2. add new content
   */
  private def prepareContent(
    ui: Node
    ) {
    _containerNode.setOpacity(0)
    _containerNode.getChildren.clear()
    _containerNode.getChildren.add(ui)

    AnchorPane.setBottomAnchor(ui, 0d)
    AnchorPane.setLeftAnchor  (ui, 0d)
    AnchorPane.setRightAnchor (ui, 0d)
    AnchorPane.setTopAnchor   (ui, 0d)
  }

  /**
   * Activate this content.
   *
   * @param key is the ui key
   * @param content is the ui to show
   */
  private def switchTo(
    key: K,
    content: ContentDescr
    ) {
    content.ui.youAreGoingActive()

    // get the currently active node
    val oldNode = if(_containerNode.getChildren.isEmpty) None else _registeredContent.find(_._2.ui.baseUINode == _containerNode.getChildren.get(0))
    val anim = oldNode match {
      case Some(x) =>
        fireNotify(OnContentInactive, x._1)
        x._2.ui.youAreGoingInactive()

        x._2.fadeOut.setOnFinished(new EventHandler[ActionEvent] {
          def handle(p1: ActionEvent) = {
            x._2.ui.youAreInactive()

            prepareContent(content.ui.baseUINode)

            fireNotify(OnContentActive, key)
            content.ui.youAreActive()

            content.fadeIn.play()
          }
        })
        content.fadeOut

      case None =>
        prepareContent(content.ui.baseUINode)

        fireNotify(OnContentActive, key)
        content.ui.youAreActive()

        content.fadeIn
    }

    Platform.runLater {
      anim.play()
    }
  }
}
