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

package de.windelknecht.stup.utils.io.pack.archive

import java.io._

import org.scalatest.{Matchers, WordSpecLike}

import scala.util.Random

class ArchiveOpsSpec
  extends WordSpecLike
  with Matchers {
  val tempDir = System.getProperty("java.io.tmpdir")

  "An ArchiveOps" when {
    "used" should {
      "create destination file" in {
        val file01 = createRandomFile("file01")
        val file02 = createRandomFile("file02")
        val dest = new File(s"$tempDir/file01.tar")

        dest.delete()

        new ArchiveOps(List(file01, file02)).archive(dest)

        dest.exists() should be (right = true)

        file01.delete()
        file02.delete()
        dest.delete()
      }
    }
  }

  def createRandomFile(fileName: String) = createFile(fileName, new Random().nextString(1024))
  def createFile(fileName: String, content: String = "") = new File(s"tempDir/$fileName")

  def writeToFile(
    file: File,
    content: String
    ) = {
    if(file.exists())
      file.delete()
    file.createNewFile()

    val writer = new FileWriter(file)

    writer.write(content)
    writer.close()

    file
  }
}
