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

package de.windelknecht.stup.utils.io

import de.windelknecht.stup.utils.coding.Implicits._
import java.io.{File, PrintWriter}
import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{FileSystems, Files, Paths}
import scala.io.Source

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 30.06.14
 * Time: 17:38
 *
 */
package object file {
  implicit class FileOps(file: File) {
    private val _extRegex = """(.*)\.(.*)$""".r

    /**
     * Return basename of this file (without extension and path)
     */
    def baseName = {
      file.getName match {
        case _extRegex(name, _) => name
        case t@_ => t
      }
    }

    /**
     * This method scans the given path/file and return all direct child files/directories as a stream.
     */
    def deepFileTree = getDeepFileTree(file)

    /**
     * Return extension of this file
     */
    def extension = {
      file.getName match {
        case _extRegex(_, ext) => ext
        case _ => ""
      }
    }

    /**
     * This method scans the given path/file and return all child-child files/directories as a stream.
     */
    def flatFileTree = getFlatFileTree(file)

    /**
     * This method scans the given path/file and return all direct child files/directories as a stream.
     */
    def maxDepthFileTree(maxDepth: Int) = getMaxDepthFileTree(maxDepth, file)

    /**
     * Read the file.
     */
    def read() = scala.io.Source.fromFile(file)

    /**
     * Return file size as long.
     */
    def sizeAsNumber = Files.size(FileSystems.getDefault.getPath(file.getAbsolutePath))

    /**
     * Return file size formatted and readable for humans..
     */
    def sizeAsString = sizeAsNumber.asHumanReadableByte

    /**
     * Print the given string into the file.
     */
    private def writer(s: String, p: PrintWriter) = p.println(s)

    /**
     * Write something to file.
     * @param op function to write into a print writer.
     */
    def write(s: String, op: (String, PrintWriter) => Unit = writer) {
      if(!file.exists()) {
        val perms = PosixFilePermissions.fromString("rwxr-x---")
        val attr = PosixFilePermissions.asFileAttribute(perms)
        Files.createDirectories(Paths.get(file.getParent), attr)
        Files.createFile(Paths.get(file.getAbsolutePath), attr)
      }

      val p = new PrintWriter(file)
      try {
        op(s,p)
      } finally {
        p.close()
      }
    }

    /**
     * Private recursive method to return all childs and child-childs of the given file.
     */
    private def getDeepFileTree(
      f: File
      ): Stream[File] = f #:: Option(f.listFiles()).toStream.flatten.flatMap(getDeepFileTree)

    /**
     * Private method to return all childs of the given file.
     */
    private def getFlatFileTree(
      f: File
      ): Stream[File] = f #:: Option(f.listFiles()).toStream.flatten

    /**
     * Private recursive method to return all childs and child-childs of the given file.
     */
    private def getMaxDepthFileTree(
      maxDepth: Int,
      f: File,
      depth: Int = 0): Stream[File] = f #:: (if(depth < maxDepth) Option(f.listFiles()) else None).toStream.flatten.flatMap{sf=> getMaxDepthFileTree(maxDepth, sf, depth + 1)}
  }

  implicit class FileNameOps(path: String)
    extends FileOps(new File(path))

  implicit class FileListOps[A <: Traversable[File]](files: A) {
    /**
     * Filter the given file collection by the given regex string.
     */
    def filterByName(regex: String) = files.filter(_.getName.matches(regex))

    /**
     * Filter out all directories
     */
    def filterNotDir = files.filterNot(_.isDirectory)

    /**
     * Filter out all directories
     */
    def filterNotLink = files.filterNot(f=> Files.isSymbolicLink(FileSystems.getDefault.getPath(f.getAbsolutePath)))

    /**
     * This method takes the file collection and group all files by its base name.
     * You will get a Map[String, A]
     */
    def groupByBaseName = files.groupBy(f=> f.baseName)

    /**
     * Read given files and pass content to the given parse function.
     * If content is undefined at op, the fully content will be returned.
     */
    def parseBy(op: PartialFunction[String, Any]) = files.map(f=> f -> (op orElse parseByDefault)(Source.fromFile(f).mkString))

    /**
     * This is the default parse function to return the complete file content.
     */
    private def parseByDefault: PartialFunction[String, Any] = { case m@_ => m }
  }
}
