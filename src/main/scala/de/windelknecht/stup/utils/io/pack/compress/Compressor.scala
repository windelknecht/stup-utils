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

package de.windelknecht.stup.utils.io.pack.compress

import de.windelknecht.stup.utils.io.ChannelTools
import de.windelknecht.stup.utils.io.pack.compress.CompressError.CompressError
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.vfs2.FileObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Compressor(
  compressThis: FileObject,
  compressTo: FileObject
  ) {
  /**
   * Start compressing.
   */
  def run(): Future[Either[CompressError, FileObject]] = Future { doWork() }

  /**
   * This method does the work.
   */
  private def doWork(): Either[CompressError, FileObject] = {
    if(!compressorToExtension.values.toSet.contains(compressTo.getName.getExtension)) {
      Left(CompressError.UnsupportedCompressor)
    } else if(!compressThis.exists()) {
      Left(CompressError.SrcFileDoesntExist)
    } else {
      if(compressTo.exists())
        compressTo.delete()

      compressTo.createFile()

      ChannelTools.fastStreamCopy(
        compressThis.getContent.getInputStream,
        new CompressorStreamFactory().createCompressorOutputStream(compressTo.getName.getExtension, compressTo.getContent.getOutputStream(false))
      )

      Right(compressTo)
    }
  }
}
