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

import de.windelknecht.stup.utils.io.ChannelTools
import de.windelknecht.stup.utils.io.pack.archive.ArchiveError.ArchiveError
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.vfs2.{VFS, FileType, FileObject}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnArchiver(
  unArchiveFrom: FileObject,
  unArchiveTo: FileObject
  ) {
  /**
   * Start un-archiving.
   */
  def run(): Future[Either[ArchiveError, (FileObject, List[FileObject])]] = Future { doWork() }

  /**
   * This method does the work.
   */
  private def doWork(): Either[ArchiveError, (FileObject, List[FileObject])] = {
    if (!archiverToExtension.values.toSet.contains(unArchiveFrom.getName.getExtension)) {
      Left(ArchiveError.UnsupportedArchive)
    } else if (unArchiveTo.getType == FileType.FILE) {
      Left(ArchiveError.CannotUnpackToAFile)
    } else {
      if(unArchiveTo.exists())
        unArchiveTo.delete()

      unArchiveTo.createFolder()

      var files = new ArrayBuffer[FileObject]()
      val in = new ArchiveStreamFactory().createArchiveInputStream(unArchiveFrom.getContent.getInputStream)

      var entry = in.getNextEntry
      while (entry != null) {
        val file = VFS.getManager.resolveFile(s"${unArchiveTo.getName}/${entry.getName}")
        file.createFile()

        ChannelTools.fastStreamCopy_doNotCloseInput(entry.getSize, in, file.getContent.getOutputStream(false))

        entry = in.getNextEntry

        files += file
      }

      Right((unArchiveTo, files.toList))
    }
  }
}
