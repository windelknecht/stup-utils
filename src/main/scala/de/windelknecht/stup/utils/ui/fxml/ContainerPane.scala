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
import de.windelknecht.stup.utils.coding.reactive.Notify
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
  case object  OnError           extends ContentPaneEvent
}

object ContainerPaneError
  extends Enumeration {
  type ContentPaneError = Value

  val CONTENT_KEY_NOT_REGISTERED = Value
}

trait ContainerPane[K] { this: Notify =>
  case class ContentDescr(ui: FxmlController, fadeIn: Animation, fadeOut: Animation)

  // fields
  protected val DEFAULT_FADING_SPEED: Double = 200d

  protected var _containerNode: AnchorPane
  private val _registeredContent = new mutable.HashMap[K, ContentDescr]()

  private lazy val _fadeIn = new Timeline(
    new KeyFrame(    jxu.Duration.ZERO,                  new KeyValue(_containerNode.opacityProperty(), double2Double(0.0))),
    new KeyFrame(new jxu.Duration(DEFAULT_FADING_SPEED), new KeyValue(_containerNode.opacityProperty(), double2Double(1.0)))
  )
  private lazy val _fadeOut = new Timeline(
    new KeyFrame(    jxu.Duration.ZERO,                  new KeyValue(_containerNode.opacityProperty(), double2Double(1.0))),
    new KeyFrame(new jxu.Duration(DEFAULT_FADING_SPEED), new KeyValue(_containerNode.opacityProperty(), double2Double(0.0)))
  )

  /**
   * Clear all registered contents.
   */
  def clearContent() {
    _registeredContent.clear()
  }

  /**
   * Add given content to pane and fade in.
   */
  private def prepareContent(
    ui: Node
    ) {
    _containerNode.setOpacity(0)
    _containerNode.getChildren.clear()
    _containerNode.getChildren.add(ui)

    AnchorPane.setBottomAnchor(ui, 0)
    AnchorPane.setLeftAnchor  (ui, 0)
    AnchorPane.setRightAnchor (ui, 0)
    AnchorPane.setTopAnchor   (ui, 0)
  }

  /**
   * Load content ui with given contentKey.
   */
  def loadContent(
    contentKey: K,
    content: FxmlController,
    fadeIn: Animation = _fadeIn,
    fadeOut: Animation = _fadeOut
    ) { _registeredContent += ( contentKey -> ContentDescr(content, fadeIn = fadeIn, fadeOut = fadeOut)) }

  /**
   * unload given content.
   *
   * TODO: remove ui
   */
  def unloadContent(contentKey: K) {
    _registeredContent -= contentKey
  }

  def shutdown() {
    Platform.runLater {
      _containerNode.setOpacity(0)
      _containerNode.getChildren.clear()
    }
    _registeredContent.clear()
  }

  /**
   * Activate this content.
   */
  def switchToContent(contentKey: K) {
    _registeredContent.get(contentKey) match {
      case Some(x) => switchTo(contentKey, x)
      case None    => fireNotify(OnError, (ContainerPaneError.CONTENT_KEY_NOT_REGISTERED, contentKey))
    }
  }

  /**
   * Activate this content.
   */
  private def switchTo(
    contentKey: K,
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

            fireNotify(OnContentActive, contentKey)
            content.ui.youAreActive()

            content.fadeIn.play()
          }
        })
        content.fadeOut

      case None =>
        prepareContent(content.ui.baseUINode)

        fireNotify(OnContentActive, contentKey)
        content.ui.youAreActive()

        content.fadeIn
    }

    Platform.runLater {
      anim.play()
    }
  }
}
