package de.windelknecht.stup.utils.tools.testing

import java.io.{BufferedWriter, OutputStreamWriter}

import org.apache.commons.vfs2.{FileObject, VFS}

import scala.util.Random

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 06.01.14
 * Time: 14:23
 *
 */
object VFSHelper {
  def createRandomFile(fileName: String) = createFile(fileName, new Random().nextString(1024))
  def createFile(fileName: String, content: String = "") = writeToFile(VFS.getManager.resolveFile(s"ram://$fileName"), content)

  def writeToFile(
    file: FileObject,
    content: String
    ) = {
    if(file.exists())
      file.delete()
    file.createFile()

    val writer = new BufferedWriter(
      new OutputStreamWriter(
        file.getContent.getOutputStream(false), "utf-8"))

    writer.write(content)
    writer.close()

    file
  }
}
