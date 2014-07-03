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
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.vfs2.{FileObject, FileType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Put the given files in the archive file.
 *
 * @param archiveThis collection of files to archive
 * @param archiveTo is the target archive file
 */
class Archiver(
  archiveThis: List[FileObject],
  archiveTo: FileObject
  ) {
  /**
   * Start archiving.
   */
  def run(): Future[Either[ArchiveError, FileObject]] = Future { doWork() }

  /**
   * This method does the work.
   */
  private def doWork(): Either[ArchiveError, FileObject] = {
    if(!archiverToExtension.values.toSet.contains(archiveTo.getName.getExtension)) {
      Left(ArchiveError.UnsupportedArchive)
    } else if(archiveThis.isEmpty) {
      Left(ArchiveError.NoFilesGiven)
    } else {
      if(archiveTo.exists())
        archiveTo.delete()

      archiveTo.createFile()

      val co = new ArchiveStreamFactory().createArchiveOutputStream(archiverToExtension.find(_._2 == archiveTo.getName.getExtension).get._1, archiveTo.getContent.getOutputStream(false))

      archiveThis
        .filter(_.getType == FileType.FILE)
        .foreach{f=>
        val entry = new TarArchiveEntry(f.getName.getPath, 0, true)

        entry.setSize(f.getContent.getSize)

        co.putArchiveEntry(entry)
        ChannelTools.fastStreamCopy_doNotCloseOutput(f.getContent.getInputStream, co)
        co.closeArchiveEntry()
      }
      co.close()

      Right(archiveTo)
    }
  }
}
