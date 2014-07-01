package de.windelknecht.stup.utils.io.file.simple

import de.windelknecht.stup.utils.coding.DoItLater
import de.windelknecht.stup.utils.coding.reactive.Notify
import de.windelknecht.stup.utils.io.file._
import java.util.UUID
import de.windelknecht.stup.utils.io.file.simple.DataProvider.{DataWritten, OnZeuch}

import scala.concurrent.duration._

/**
 * TODO: no file watching implemented
 */
object AsyncFileIO {
  /**
   * Create-fn for easier java usage.
   */
  def create(
    fileName: String
  ) = new AsyncFileIO(fileName)

  /**
   * Create-fn for easier java usage.
   */
  def create(
    fileName: String,
    writeTimeoutInSeconds: Int
  ) = new AsyncFileIO(fileName, writeTimeoutInSeconds seconds)
}

class AsyncFileIO(
  fileName: String,
  writeTimeout: Duration = 10.seconds
  )
  extends DataProvider
  with Notify
  with DoItLater {
  // fields
  private val _writeId = UUID.randomUUID()

  /**
   * Reads all data and return as string list.
   */
  override def read() = readCompleteFile()

  /**
   * Write the given data
   * @param content is the data to be written
   * @param force write now or at your own timed schedule
   */
  override def write(content: String, force: Boolean) = {
    doIt(
      id = _writeId,
      time = writeTimeout
      ) {
      fileName.write(content)

      fireNotify(OnZeuch, DataWritten)
    }
  }

  /**
   * Read the given file and return all rows as a string list.
   */
  private def readCompleteFile(): List[String] = fileName.read().getLines().toList
}
