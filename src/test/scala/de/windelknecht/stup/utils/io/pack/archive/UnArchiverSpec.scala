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

import de.windelknecht.stup.utils.tools.testing.VFSHelper
import org.apache.commons.vfs2.VFS
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class UnArchiverSpec
  extends WordSpecLike
  with Matchers {
  "An unArchiver object" when {
    "fired an erroneous archiveTo" should {
      "reply an error when unsupported archive extension" in {
        Await.result(new UnArchiver(VFSHelper.createRandomFile("dir01/file01.tars"), VFS.getManager.resolveFile("ram://dir002/out/")).run(), 1 second).fold(
          error => error,
          unArchiveTo => unArchiveTo
        ) should be (ArchiveError.UnsupportedArchive)
      }

      "reply an error when dest is not a dir" in {
        Await.result(new UnArchiver(VFSHelper.createRandomFile("dir01/file01.tar"), VFSHelper.createRandomFile("dir01/so.file")).run(), 1 second).fold(
          error => error,
          unArchiveTo => unArchiveTo
        ) should be (ArchiveError.CannotUnpackToAFile)
      }
    }
  }
}
