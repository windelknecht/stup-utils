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

import _root_.javafx.embed.swing.SwingFXUtils
import _root_.javafx.geometry.VPos
import _root_.javafx.scene.SnapshotParameters
import _root_.javafx.scene.canvas.Canvas
import _root_.javafx.scene.image.{WritableImage, Image}
import _root_.javafx.scene.paint.{Color, Paint}
import _root_.javafx.scene.text.{TextAlignment, Font}
import java.awt.image.BufferedImage
import java.io.{OutputStream, File, IOException}
import javax.imageio.ImageIO

object ImageHelper {
  /**
   * This function converts a javafx image into an awt image (e.g. can be written to file).
   */
  def fromFXImage(image: Image): BufferedImage = SwingFXUtils.fromFXImage(image, null)

  /**
   * This function converts an awt image into a javafx image.
   */
  def toFXImage(image: BufferedImage): WritableImage = SwingFXUtils.toFXImage(image, null)

  /**
   * This method converts a char, font glyph into an image.
   *
   * @param height output height
   * @param width output width
   * @param fontName is the name of the font
   * @param foreColor is the color of the char
   * @param backColor is the background color
   * @param text is the char to paint (can be a unicode char too)
   * @param pixelToFontSizeFactor Note that the Font constructor doesn’t take a pixel size, but rather a point size specifying the font’s baseline distance.
   *                              This means you cannot a priori determine the correct Font size to precisely fill our Canvas with any given icon.
   *                              The Canvas pixel size is usually a good heuristic for the Font point size, but then you’ll simply have to check the output and tweak
   *                              as necessary, e.g. with the factor 1.3 shown here.
   * @return
   */
  def fontToImage(
    height: Double = 256,
    width: Double = 256,
    fontName: String,
    foreColor: Paint = Color.BLACK,
    backColor: Paint = Color.TRANSPARENT,
    text: String,
    pixelToFontSizeFactor: Double = 1.3
    ): Image = {
    val canvas = new Canvas(height, width)
    val gc = canvas.getGraphicsContext2D

    // create font + write char
    val font = Font.loadFont(fontName, pixelToFontSizeFactor * math.min(height, width))
    gc.setFont(font)
    gc.setTextAlign(TextAlignment.CENTER)
    gc.setTextBaseline(VPos.CENTER)
    gc.setFill(foreColor)
    gc.fillText(text, height / 2, width / 2)

    // prepare snapshot
    val snapshotParams = new SnapshotParameters
    snapshotParams.setFill(backColor)

    canvas.snapshot(snapshotParams, null)
  }

  /**
   * This function write a javafx image into the given file.
   */
  @throws(classOf[IOException])
  def writeImage(imageToWrite: Image, output: File) = ImageIO.write(fromFXImage(imageToWrite), "png", output)

  /**
   * This function write a javafx image into the given stream.
   */
  @throws(classOf[IOException])
  def writeImage(imageToWrite: Image, output: OutputStream) = ImageIO.write(fromFXImage(imageToWrite), "png", output)
}
