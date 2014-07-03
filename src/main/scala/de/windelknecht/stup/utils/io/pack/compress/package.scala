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
import de.windelknecht.stup.utils.io.pack.compress.CompressError.CompressError
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.vfs2.FileObject

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 03.07.14
 * Time: 17:19
 *
 */
package object compress {
  private[compress] val compressorToExtension = Map(
    CompressorStreamFactory.GZIP -> "gz",
    CompressorStreamFactory.BZIP2 -> "bz2"
    //CompressorStreamFactory.XZ -> "xz",
    //CompressorStreamFactory.LZMA -> "lzma",
    //CompressorStreamFactory.PACK200 -> "pack"
  )

  implicit class CompressOps(
    src: File
    ) {
    /**
     * Archive given files.
     */
    def compress(dest: File): Either[CompressError, File] = {
      toFileObject(src)
        .compress(toFileObject(dest))
        .fold(
        error => Left(error),
        _ => Right(dest)
        )
    }

    /**
     * Archive given files.
     */
    def compress(destFilename: String): Either[CompressError, File] = compress(new File(destFilename))
  }

  implicit class CompressOnVFSOps(
    src: FileObject
    ) {
    /**
     * Un-archive given files.
     */
    def compress(dest: FileObject): Either[CompressError, FileObject] = Await.result(new Compressor(src, dest).run(), 1 second)
  }

  implicit class UnCompressOps(
    src: File
    ) {
    /**
     * Un-archive given files.
     *
     * @param dest is the destination dir where the unarchived files will be putted in
     * @return (1, 2): 1=destination dir, 2=unarchived files
     */
    def unCompress(dest: File): Either[CompressError, File] = {
      toFileObject(src)
        .unCompress(toFileObject(dest))
        .fold(
        error => Left(error),
        _ => Right(dest)
        )
    }

    /**
     * Un-archive given files.
     *
     * @param destFilename is the destination dir where the unarchived files will be putted in
     * @return (1, 2): 1=destination dir, 2=unarchived files
     */
    def unCompress(destFilename: String): Either[CompressError, File] = unCompress(new File(destFilename))
  }

  implicit class UnCompressOnVFSOps(
    src: FileObject
    ) {
    /**
     * Un-archive given files.
     *
     * @param dest is the destination dir where the unarchived files will be putted in
     * @return (1, 2): 1=destination dir, 2=unarchived files
     */
    def unCompress(dest: FileObject): Either[CompressError, FileObject] = Await.result(new UnCompressor(src, dest).run(), 1 second)
  }
}
