package de.windelknecht.stup.utils.coding.mvc.dao

import java.io.{InputStream, OutputStream}

import de.windelknecht.stup.utils.coding.mvc.{Entity, DaoIsCloseable}
import de.windelknecht.stup.utils.io.vfs._
import org.apache.commons.vfs2.{FileType, FileObject}

trait DaoSerializer {
  /**
   * Serialize content into output stream.
   */
  def run(data: List[Entity], stream: OutputStream): Unit
}
trait DaoDeSerializer {
  /**
   * Load from stream and deserialize.
   */
  def run(stream: InputStream): List[Entity]
}

class NotAFileException(m: String) extends Exception

abstract class FileDao(
  file: FileObject,
  serializer: DaoSerializer,
  deSerializer: DaoDeSerializer
  )
  extends MemoryDao
  with DaoIsCloseable {
  require(file.exists(), "file does not exist")

  // ctor
  init()

  /**
   * Close data source.
   */
  override def close(): Unit = {
    // close file
    file.synchronized {
      if(!file.isWriteable)
        return

      serializer.run(read(), file.getContent.getOutputStream)
      file.close()
    }
  }

  /**
   * Does a complete file read and read complete file into cache
   */
  private def init(): Unit = {
    if (file.getType != FileType.FILE)
      throw new NotAFileException(s"given file '$file' is not of type file (${file.getType}})")

    if (file.isEmpty)
      return

    deSerializer
      .run(file.getContent.getInputStream)
      .foreach(update)
  }
}
