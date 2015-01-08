package de.windelknecht.stup.utils.coding.mvc.dao

import de.windelknecht.stup.utils.coding.mvc.Entity
import org.apache.commons.vfs2.{FileType, FileObject}

import de.windelknecht.stup.utils.io.vfs._

import scala.pickling._, json._

class NotAFileException(m: String) extends Exception
class NotWritableException(m: String) extends Exception

class JsonDao(
  file: FileObject
  )
  extends MemoryDao {
  // ctor
  init()

  /**
   * Close data source.
   */
  override def close() = {
    // close file
    file.synchronized {
      file.write(read().pickle.value)
      file.close()
    }

    super.close()
  }

  /**
   * Does a complete file read and read complete file into cache
   */
  private def init(): Unit = {
    if (!file.exists())
      return

    if (file.getType != FileType.FILE)
      throw new NotAFileException(s"given file '$file' is not of type file (${file.getType}})")

    if (!file.isWriteable)
      throw new NotWritableException(s"given file '$file' is not writeable")

    if (file.isEmpty)
      return

    file
      .read()
      .mkString
      .unpickle[List[Entity]]
      .foreach(update)
  }
}
