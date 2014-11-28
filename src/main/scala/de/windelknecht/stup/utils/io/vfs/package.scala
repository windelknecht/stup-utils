package de.windelknecht.stup.utils.io

import java.io.{OutputStreamWriter, PrintWriter}
import java.nio.file.{FileSystems, Files}

import de.windelknecht.stup.utils.coding.Implicits._
import org.apache.commons.vfs2.{FileType, VFS, FileObject}

import scala.io.{Codec, Source}

package object vfs {
  implicit class FileOps(file: FileObject) {
    /**
     * Return basename of this file (without extension and path)
     */
    def baseName = file.getName.getBaseName

    /**
     * This method scans the given path/file and return all direct child files/directories as a stream.
     */
    def deepFileTree = getDeepFileTree(file)

    /**
     * Return extension of this file
     */
    def extension = file.getName.getExtension

    /**
     * This method scans the given path/file and return all child-child files/directories as a stream.
     */
    def flatFileTree = getFlatFileTree(file)

    /**
     * This method scans the given path/file and return all direct child files/directories as a stream.
     */
    def maxDepthFileTree(maxDepth: Int) = getMaxDepthFileTree(maxDepth, file)

    /**
     * Return full name incl path.
     */
    def name = file.getName.getPath

    /**
     * Read the file.
     */
    def read()(implicit codec: Codec) = Source.fromInputStream(file.getContent.getInputStream)

    /**
     * Return file size as long.
     */
    def sizeAsNumber = file.getContent.getSize

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
    def write(
      s: String,
      append: Boolean = false,
      op: (String, PrintWriter) => Unit = writer
      )(implicit codec: Codec) {
      if(!file.exists()) {
        file.createFile()
        // rwxr-x---
        file.setExecutable(true)
        file.setReadable(true)
        file.setWritable(true, true) // ownerOnly
      }

      val p = new PrintWriter(new OutputStreamWriter(file.getContent.getOutputStream(append), codec.charSet))
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
      f: FileObject
      ): Stream[FileObject] = f #:: Option(f.getChildren).toStream.flatten.flatMap(getDeepFileTree)

    /**
     * Private method to return all childs of the given file.
     */
    private def getFlatFileTree(
      f: FileObject
      ): Stream[FileObject] = f #:: Option(f.getChildren).toStream.flatten

    /**
     * Private recursive method to return all childs and child-childs of the given file.
     */
    private def getMaxDepthFileTree(
      maxDepth: Int,
      f: FileObject,
      depth: Int = 0): Stream[FileObject] = f #:: (if(depth < maxDepth) Option(f.listFiles()) else None).toStream.flatten.flatMap{sf=> getMaxDepthFileTree(maxDepth, sf, depth + 1)}
  }

  implicit class FileNameOps(path: String)
    extends FileOps(VFS.getManager.resolveFile(path))

  implicit class FileListOps[A <: Traversable[FileObject]](files: A) {
    /**
     * Filter the given file collection by the given regex string.
     */
    def filterByName(regex: String) = files.filter(_.name.matches(regex))

    /**
     * Filter out all directories
     */
    def filterNotDir = files.filterNot(_.getType == FileType.FOLDER)

    /**
     * Filter out all directories
     */
    def filterNotLink = files.filterNot(f=> Files.isSymbolicLink(FileSystems.getDefault.getPath(f.name)))

    /**
     * This method takes the file collection and group all files by its base name.
     * You will get a Map[String, A]
     */
    def groupByBaseName = files.groupBy(f=> f.baseName)

    /**
     * Read given files and pass content to the given parse function.
     * If content is undefined at op, the fully content will be returned.
     */
    def parseBy(op: PartialFunction[String, Any]) = files.map(f=> f -> (op orElse parseByDefault)(f.read().mkString))

    /**
     * This is the default parse function to return the complete file content.
     */
    private def parseByDefault: PartialFunction[String, Any] = { case m@_ => m }
  }
}
