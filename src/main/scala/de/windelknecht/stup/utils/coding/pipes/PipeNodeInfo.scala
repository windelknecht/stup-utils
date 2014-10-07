package de.windelknecht.stup.utils.coding.pipes

import java.util.UUID
import scala.reflect.ClassTag

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 11.04.14
 * Time: 09:47
 *
 */
object PipeNodeInfo {
  def apply[T](
    id: UUID = UUID.randomUUID(),
    name: String
    )(implicit classTag: ClassTag[T]) = new PipeNodeInfo(id, name, classTag.runtimeClass.getName)
}

case class PipeNodeInfo(
  id: UUID,
  name: String,
  typeInfo: String
  )
