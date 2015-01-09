package de.windelknecht.stup.utils.coding.mvc.dao.serialize

import java.io.{InputStream, OutputStream}
import java.nio.charset.Charset

import de.windelknecht.stup.utils.coding.mvc.Entity
import de.windelknecht.stup.utils.coding.mvc.dao.{DaoDeSerializer, DaoSerializer}
import scala.pickling._, json._

sealed trait Json

class JsonDaoSerializer()
  extends DaoSerializer {
  /**
   * Serialize content into output stream.
   */
  def run(
    data: List[Entity],
    stream: OutputStream
    ): Unit = {
    val r1 = data.pickle
    val r2 = r1.value
    val r3 = r2.getBytes(Charset.forName("UTF-8"))

//    stream.write()

    stream.write(data.pickle.value.getBytes(Charset.forName("UTF-8")))
    stream.flush()
  }
}

class JsonDaoDeSerializer()
  extends DaoDeSerializer {
  /**
   * Load from stream and deserialize.
   */
  def run(
    stream: InputStream
    ): List[Entity] = {
    scala
      .io
      .Source
      .fromInputStream(stream)
      .getLines()
      .mkString("\n")
      .unpickle[List[Entity]]
  }
}
