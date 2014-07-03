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

package de.windelknecht.stup.utils.io.pack

import java.io.File

import de.windelknecht.stup.utils.coding.Implicits._
import de.windelknecht.stup.utils.io.pack.archive.ArchiveError.ArchiveError
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.vfs2.FileObject

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 03.07.14
 * Time: 14:36
 *
 */
package object archive {
  private[archive] val archiverToExtension = Map(
    ArchiveStreamFactory.AR -> "ar",
    ArchiveStreamFactory.ARJ -> "arj",
    ArchiveStreamFactory.CPIO -> "cpio",
    ArchiveStreamFactory.DUMP -> "dmp",
    ArchiveStreamFactory.JAR -> "jar",
    ArchiveStreamFactory.TAR -> "tar",
    ArchiveStreamFactory.ZIP -> "zip"
  )

  implicit class ArchiveOps(
    src: List[File]
    ) {
    /**
     * Archive given files.
     */
    def archive(dest: File): Either[ArchiveError, File] = {
      src
        .map(toFileObject)
        .archive(toFileObject(dest))
        .fold(
        error => Left(error),
        _ => Right(dest)
        )
    }

    /**
     * Archive given files.
     */
    def archive(destFilename: String): Either[ArchiveError, File] = archive(new File(destFilename))
  }

  implicit class ArchiveOnVFSOps(
    src: List[FileObject]
    ) {
    /**
     * Un-archive given files.
     */
    def archive(dest: FileObject): Either[ArchiveError, FileObject] = Await.result(new Archiver(src, dest).run(), 1 second)
  }

  implicit class UnArchiveOps(
    src: File
    ) {
    /**
     * Un-archive given files.
     *
     * @param dest is the destination dir where the unarchived files will be putted in
     * @return (1, 2): 1=destination dir, 2=unarchived files
     */
    def unArchive(dest: File): Either[ArchiveError, (File, List[File])] = {
      toFileObject(src)
        .unArchive(toFileObject(dest))
        .fold(
        error => Left(error),
        res => Right((toFile(res._1), res._2.map(toFile)))
        )
    }

    /**
     * Un-archive given files.
     *
     * @param destFilename is the destination dir where the unarchived files will be putted in
     * @return (1, 2): 1=destination dir, 2=unarchived files
     */
    def unArchive(destFilename: String): Either[ArchiveError, (File, List[File])] = unArchive(new File(destFilename))
  }

  implicit class UnArchiveOnVFSOps(
    src: FileObject
    ) {
    /**
     * Un-archive given files.
     *
     * @param dest is the destination dir where the unarchived files will be putted in
     * @return (1, 2): 1=destination dir, 2=unarchived files
     */
    def unArchive(dest: FileObject): Either[ArchiveError, (FileObject, List[FileObject])] = Await.result(new UnArchiver(src, dest).run(), 1 second)
  }
}
