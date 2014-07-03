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

import de.windelknecht.stup.utils.security.ChecksumTools
import de.windelknecht.stup.utils.tools.testing.VFSHelper
import org.apache.commons.vfs2.{FileObject, VFS}
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 03.07.14
 * Time: 15:39
 *
 */
class ArchiverSpec
  extends WordSpecLike
  with Matchers {
  "An archiver object" when {
    "fired an erroneous archiveTo" should {
      "reply an error when unsupported archive extension" in {
        val ar = new Archiver(
          List(VFSHelper.createRandomFile("dir01/file01")),
          VFSHelper.createRandomFile("dir01/file01.tars")
        )

        Await.result(ar.run(), 1 second).fold(
          error =>  error,
          archiveTo => archiveTo
        ) should be (ArchiveError.UnsupportedArchive)
      }

      "reply an error when no files are given" in {
        val ar = new Archiver(
          List.empty,
          VFSHelper.createRandomFile("dir01/file01.tar")
        )

        Await.result(ar.run(), 1 second).fold(
          error =>  error,
          archiveTo => archiveTo
        ) should be (ArchiveError.NoFilesGiven)
      }
    }

    "archive to a tar archive" should {
      "return a new file object with tar extension" in {
        val archiveTo = VFSHelper.createRandomFile("dir01/file01.tar")
        val ar = new Archiver(
          List(
            VFSHelper.createRandomFile("dir01/file01"),
            VFSHelper.createRandomFile("dir01/file02")
          ),
          archiveTo
        )

        Await.result(ar.run(), 1 second).fold(
          error =>  error,
          archiveTo => archiveTo
        ) should be (archiveTo)
      }

      "return a valid archive" in {
        val file1 = VFSHelper.createRandomFile("dir01/file01")
        val file2 = VFSHelper.createRandomFile("dir01/file02")
        val archiveTo = VFSHelper.createRandomFile("dir01/file01.tar")
        val ar = new Archiver(
          List(
            file1,
            file2
          ),
          archiveTo
        )

        val archive = Await.result(ar.run(), 1 second).fold(
          error =>  error,
          archiveTo => archiveTo
        ).asInstanceOf[FileObject]
        val outDir = VFS.getManager.resolveFile("ram://dir002/out/")

        val (out, fl) = Await.result(new UnArchiver(archive, outDir).run(), 1 second).fold(
          error => error,
          unArchiveTo => unArchiveTo
        ).asInstanceOf[(FileObject, List[FileObject])]
        val files = fl.map{f=> (f.getName.toString.replace(outDir.getName.toString, "ram://"), f)}.toMap

        out should be (outDir)
        files.size should be (2)
        files.get(file1.getName.toString) should not be None
        files.get(file2.getName.toString) should not be None

        ChecksumTools.fromVFSFile(file1) should be (ChecksumTools.fromVFSFile(files(file1.getName.toString)))
        ChecksumTools.fromVFSFile(file2) should be (ChecksumTools.fromVFSFile(files(file2.getName.toString)))
      }

      "return a valid archive when compressed twice" in {
        val file1 = VFSHelper.createRandomFile("dir01/file01")
        val file2 = VFSHelper.createRandomFile("dir01/file02")
        val archiveTo = VFSHelper.createRandomFile("dir01/file01.tar")
        val ar = new Archiver(
          List(
            file1,
            file2
          ),
          archiveTo
        )

        Await.ready(new Archiver(List(file1, file2), archiveTo).run(), 1 second)

        VFSHelper.writeToFile(file1, "kk")
        VFSHelper.writeToFile(file1, "kkddedede")

        val archive = Await.result(ar.run(), 1 second).fold(
           error =>  error,
            archiveTo => archiveTo
          ).asInstanceOf[FileObject]
        val outDir = VFS.getManager.resolveFile("ram://dir002/out/")

        val (out, fl) = Await.result(new UnArchiver(archive, outDir).run(), 1 second).fold(
          error => error,
          unArchiveTo => unArchiveTo
        ).asInstanceOf[(FileObject, List[FileObject])]
        val files = fl.map{f=> (f.getName.toString.replace(outDir.getName.toString, "ram://"), f)}.toMap

        out should be (outDir)
        files.size should be (2)
        files.get(file1.getName.toString) should not be None
        files.get(file2.getName.toString) should not be None

        ChecksumTools.fromVFSFile(file1) should be (ChecksumTools.fromVFSFile(files(file1.getName.toString)))
        ChecksumTools.fromVFSFile(file2) should be (ChecksumTools.fromVFSFile(files(file2.getName.toString)))
      }
    }
  }
}
