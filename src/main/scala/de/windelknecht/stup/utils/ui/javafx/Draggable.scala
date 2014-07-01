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

package de.windelknecht.stup.utils.ui.javafx

import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.{Cursor, Node}

trait Draggable {this: Node=>
  // fields
  private var _dragAnchor = Point2D.ZERO
  private var _dragIsEnabled = true
  private var _isDragging = false

  // ctor
  setOnMouseEntered(new EventHandler[MouseEvent] {
    override def handle(evt: MouseEvent) = {
      setCursor(Cursor.DEFAULT)
    }
  })

  setOnMousePressed(new EventHandler[MouseEvent] {
    override def handle(evt: MouseEvent) = {
      if(_dragIsEnabled) {
        _isDragging = true

        _dragAnchor = new Point2D(
          getLayoutX - evt.getSceneX,
          getLayoutY - evt.getSceneY
        )
        setCursor(Cursor.MOVE)
      }
    }
  })

  setOnMouseDragged(new EventHandler[MouseEvent] {
    override def handle(evt: MouseEvent) = {
      if(_isDragging) {
        setLayoutX(evt.getSceneX + _dragAnchor.getX)
        setLayoutY(evt.getSceneY + _dragAnchor.getY)
      }
    }
  })

  setOnMouseReleased(new EventHandler[MouseEvent] {
    override def handle(evt: MouseEvent) = {
      _isDragging = false

      if(_dragIsEnabled) {
        setCursor(Cursor.DEFAULT)
      }
    }
  })

  protected def dragIsEnabled = _dragIsEnabled
  protected def dragIsEnabled_=(v: Boolean) { _dragIsEnabled = v }
}
