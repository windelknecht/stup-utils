package de.windelknecht.stup.utils.coding.mvc

import java.util.UUID
import akka.actor.ActorRef
import de.windelknecht.stup.utils.tools.RandomHelper
import org.mockito.Mockito._
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mock.MockitoSugar

case class ms_entityMock_complex(
  id: String = UUID.randomUUID().toString,
  name: String = "",
  description: String = ""
  ) extends Entity

case class ms_entityMock_simple(
  id: String = UUID.randomUUID().toString
  ) extends Entity

class ms_modelMock_complex(
  dao: Dao,
  entityId: String,
  modelHandler: ActorRef
  )
  extends Model[ms_entityMock_complex](
    dao = dao,
    entityId = entityId,
    modelHandler = modelHandler
    ) {
  def getDao = dao
  def getModelHandler = modelHandler
}

class ms_modelMock_simple(
  dao: Dao,
  entityId: String,
  modelHandler: ActorRef
  )
  extends Model[ms_entityMock_simple](
    dao = dao,
    entityId = entityId,
    modelHandler = modelHandler
    ) {
  def getDao = dao
  def getModelHandler = modelHandler
}

class ModelSpec
  extends WordSpec
  with Matchers
  with MockitoSugar {
  "instantiating the model" when {
    "the wanted entity exists" should {
      "the dao is called" in {
        val m = mock[Dao]
        val id = UUID.randomUUID().toString

        when(m.read(id)).thenReturn(Some(ms_entityMock_complex(id)))

        new ms_modelMock_complex(m, id, null)

        Thread.sleep(50)
        verify(m).read(id)
      }

      "thrown an exception when wrong type" in {
        val m = mock[Dao]
        val id = UUID.randomUUID().toString

        when(m.read(id)).thenReturn(Some(ms_entityMock_simple(id)))

        an[IllegalStateException] should be thrownBy{ new ms_modelMock_complex(m, id, null).id }
      }
    }

    "the wanted entity doesnt exists" should {
      "the dao.read is called" in {
        val m = mock[Dao]
        val id = UUID.randomUUID().toString

        when(m.read(id)).thenReturn(None)
        when(m.create(classOf[ms_entityMock_complex].getName)).thenReturn(ms_entityMock_complex(id))

        new ms_modelMock_complex(m, id, null).id

        verify(m).read(id)
      }

      "the dao.create is called" in {
        val m = mock[Dao]
        val id = UUID.randomUUID().toString

        when(m.read(id)).thenReturn(None)
        when(m.create(classOf[ms_entityMock_complex].getName)).thenReturn(ms_entityMock_complex(id))

        new ms_modelMock_complex(m, id, null).id

        verify(m).create(classOf[ms_entityMock_complex].getName)
      }
    }
  }

  "calling getter" when {
    "the getter name exists" should {
      "return the id value" in {
        val (_, sm, e) = createMock()
        sm.id should be (e.id)
      }

      "return the name value" in {
        val (_, sm, e) = createMock()
        val name: String = sm.name

        name should be (e.name)
      }

      "return the description value" in {
        val (_, sm, e) = createMock()
        val description: String = sm.description

        description should be (e.description)
      }
    }

    "the getter name doesnt exists" should {
      "thrown an exception" in {
        val (_, sm, _) = createMock()

        an[IllegalArgumentException] should be thrownBy { sm.sumsi }
      }
    }

    "calling setter" when {
      "the setter name exists" should {
        "call the dao" in {
          val (dao, sm, e) = createMock()

          sm.name = "jskjsk"

          Thread.sleep(100)
          verify(dao).update(ms_entityMock_complex(e.id, "jskjsk", e.description))
        }
      }

      "the setter name doesnt exists" should {
        "call the dao" in {
          val (_, sm, _) = createMock()

          an[IllegalArgumentException] should be thrownBy { sm.names = "jskjsk" }
        }
      }
    }
  }

  private def createMock(): (Dao, ms_modelMock_complex, ms_entityMock_complex) = {
    val m = mock[Dao]
    val e = ms_entityMock_complex(name = RandomHelper.rndString(), description = RandomHelper.rndString())

    when(m.read(e.id)).thenReturn(Some(e))

    (m, new ms_modelMock_complex(m, e.id, null), e)
  }
}
