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

package de.windelknecht.stup.utils.security

import java.io.{File, FileInputStream, InputStream}
import java.security.MessageDigest
import org.apache.commons.vfs2.FileObject

object ChecksumTools {
  val MD2     = "MD2"
  val MD5     = "MD5"
  val SHA_1   = "SHA-1"
  val SHA_256 = "SHA-256"
  val SHA_384 = "SHA-384"
  val SHA_512 = "SHA-512"


  /**
   * Build check sum from string.
   */
  def fromString(s: String)(algorithm: String = SHA_256): Array[Byte] = {
    val md = MessageDigest.getInstance(algorithm.toString)

    md.update(s.getBytes)
    md.digest()
  }

  /**
   * Build check sum from file.
   */
  def fromFile(
    file: File,
    algorithm: String = SHA_256
    ): Array[Byte] = fromIS(new FileInputStream(file), algorithm)

  /**
   * Build check sum from apache VFS file.
   */
  def fromVFSFile(
    file: FileObject,
    algorithm: String = SHA_256
    ): Array[Byte] = fromIS(file.getContent.getInputStream, algorithm)

  /**
   * Build check sum from inputstream.
   */
  def fromIS(
    is: InputStream,
    algorithm: String = SHA_256
    ): Array[Byte] = {
    val md = MessageDigest.getInstance(algorithm.toString)
    val ar = new Array[Byte](1024*1024)
    var len = is.read(ar)

    while(len != -1) {
      md.update(ar, 0, len)
      len = is.read(ar)
    }

    md.digest()
  }
}