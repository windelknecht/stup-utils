package de.windelknecht.stup.utils.coding.mvc

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, DefaultTimeout, ImplicitSender}
import java.util.UUID
import de.windelknecht.stup.utils.coding.mvc.ModelHandler._
import de.windelknecht.stup.utils.coding.mvc.Select.ModelListRes
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{WordSpecLike, BeforeAndAfterAll, Matchers}
import scala.concurrent.duration._

class DaoMock
  extends Dao {
  override def close() = {}
  override def delete(id: String) {}
  override def read(id: String) = None
  override def read() = null
  override def update[T <: Entity](entity: T) = null
}

case class mhs_entityMock_simple1(
  id: String = UUID.randomUUID().toString
  ) extends Entity

case class mhs_entityMock_simple2(
  id: String = UUID.randomUUID().toString
  ) extends Entity

class mhs_modelMock_simple1(
  dao: Dao,
  entityId: String,
  modelHandler: ActorRef
  )
  extends Model[mhs_entityMock_simple1](
    dao = dao,
    entityId = entityId,
    modelHandler = modelHandler
    ) {
  def getDao = dao
  def getModelHandler = modelHandler
}

class mhs_modelMock_simple2(
  dao: Dao,
  entityId: String,
  modelHandler: ActorRef
  )
  extends Model[mhs_entityMock_simple2](
    dao = dao,
    entityId = entityId,
    modelHandler = modelHandler
    ) {
  def getDao = dao
  def getModelHandler = modelHandler
}

class ModelHandlerSpec
  extends TestKit(ActorSystem("ModelHandlerSpec"))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll
  with MockitoSugar {
  implicit val actorSystem = ActorSystem("ModelHandlerSpec")

  "the handler class gets fired with 'Create' messages" when {
    "the wanted entity exists" should {
      "return CreateSuccess message" in {
        ModelHandler
          .create(
            new DaoMock(),
            "de.windelknecht.stup"
          ) ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple1")

        val cs = receiveN(1, 5 seconds)(0)
        cs.isInstanceOf[CreateSuccess] should be (right = true)
      }

      "return mhs_modelMock_simple1 model object" in {
        ModelHandler
          .create(
            new DaoMock(),
            "de.windelknecht.stup"
          ) ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple1")

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[CreateSuccess]
        cs.model.isInstanceOf[mhs_modelMock_simple1] should be (right = true)
      }

      "return correct CreateSuccess(..., model: mhs_modelMock_simple1.dao)" in {
        val dao = new DaoMock()

        ModelHandler
          .create(
            dao,
            "de.windelknecht.stup"
          ) ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple1")

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[CreateSuccess]
        cs.model.asInstanceOf[mhs_modelMock_simple1].getDao should be (dao)
      }

      "return correct CreateSuccess(..., model: mhs_modelMock_simple1.modelHandler)" in {
        val actor = ModelHandler.create(new DaoMock(), "de.windelknecht.stup")

        actor ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple1")

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[CreateSuccess]
        cs.model.asInstanceOf[mhs_modelMock_simple1].getModelHandler should be (actor)
      }
    }

    "use a second entity" should {
      "return CreateSuccess message" in {
        ModelHandler
          .create(
            new DaoMock(),
            "de.windelknecht.stup"
          ) ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple2")

        val cs = receiveN(1, 5 seconds)(0)
        cs.isInstanceOf[CreateSuccess] should be (right = true)
      }

      "return mhs_modelMock_simple2 model object" in {
        ModelHandler
          .create(
            new DaoMock(),
            "de.windelknecht.stup"
          ) ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple2")

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[CreateSuccess]
        cs.model.isInstanceOf[mhs_modelMock_simple2] should be (right = true)
      }

      "return correct CreateSuccess(model: mhs_modelMock_simple2.dao)" in {
        val dao = new DaoMock()

        ModelHandler
          .create(
            dao,
            "de.windelknecht.stup"
          ) ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple2")

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[CreateSuccess]
        cs.model.asInstanceOf[mhs_modelMock_simple2].getDao should be (dao)
      }

      "return correct CreateSuccess(model: mhs_modelMock_simple2.modelHandler)" in {
        val actor = ModelHandler.create(new DaoMock(), "de.windelknecht.stup")

        actor ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple2")

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[CreateSuccess]
        cs.model.asInstanceOf[mhs_modelMock_simple2].getModelHandler should be (actor)
      }
    }

    "the wanted entity not exists" should {
      "return CreateFailure message" in {
        ModelHandler
          .create(
            new DaoMock(),
            "de.windelknecht.stup"
          ) ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple155")

        val cs = receiveN(1, 5 seconds)(0)
        cs.isInstanceOf[CreateFailure] should be(right = true)
      }
    }

    "the wanted entity has a stup model with incomplete defined ctor" should {
      "return CreateFailure message" in {
        ModelHandler
          .create(
            new DaoMock(),
            "de.windelknecht.stup"
          ) ! ModelHandler.Create("de.windelknecht.stup.utils.coding.mvc.mhs_entityMock_simple13")

        val cs = receiveN(1, 5 seconds)(0)
        cs.isInstanceOf[CreateFailure] should be(right = true)
      }
    }
  }

  "the handler class gets fired with 'Read' messages" when {
    "the entity exist" should {
      "call the dao" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString

        when(m.read(id)).thenReturn(Some(mhs_entityMock_simple1(id)))

        ModelHandler
          .create(
            m,
            "de.windelknecht.stup"
          ) ! ModelHandler.Read(id)

        receiveN(1, 5 seconds)(0)
        verify(m, times(2)).read(id)
      }

      "return a ReadSuccess" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString

        when(m.read(id)).thenReturn(Some(mhs_entityMock_simple1(id)))

        ModelHandler
          .create(
            m,
            "de.windelknecht.stup"
          ) ! ModelHandler.Read(id)

        val cs = receiveN(1, 5 seconds)(0)
        cs.isInstanceOf[ReadSuccess] should be(right = true)
      }

      "return correct ReadSuccess(model: mhs_modelMock_simple1)" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString

        when(m.read(id)).thenReturn(Some(mhs_entityMock_simple1(id)))

        ModelHandler
          .create(
            m,
            "de.windelknecht.stup"
          ) ! ModelHandler.Read(id)

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[ReadSuccess]
        cs.model.isInstanceOf[mhs_modelMock_simple1] should be(right = true)
      }

      "return correct ReadSuccess(model: mhs_modelMock_simple1.id)" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString

        when(m.read(id)).thenReturn(Some(mhs_entityMock_simple1(id)))

        ModelHandler
          .create(
            m,
            "de.windelknecht.stup"
          ) ! ModelHandler.Read(id)

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[ReadSuccess]
        cs.model.asInstanceOf[mhs_modelMock_simple1].id should be(id)
      }

      "return correct ReadSuccess(model: mhs_modelMock_simple1.dao)" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString

        when(m.read(id)).thenReturn(Some(mhs_entityMock_simple1(id)))

        ModelHandler
          .create(
            m,
            "de.windelknecht.stup"
          ) ! ModelHandler.Read(id)

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[ReadSuccess]
        cs.model.asInstanceOf[mhs_modelMock_simple1].getDao should be(m)
      }

      "return correct ReadSuccess(model: mhs_modelMock_simple1.modelHandler)" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString
        val actor = ModelHandler.create(m, "de.windelknecht.stup")

        when(m.read(id)).thenReturn(Some(mhs_entityMock_simple1(id)))

        actor ! ModelHandler.Read(id)

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[ReadSuccess]
        cs.model.asInstanceOf[mhs_modelMock_simple1].getModelHandler should be(actor)
      }
    }

    "the entity not exist" should {
      "call the dao" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString

        when(m.read(id)).thenReturn(None)

        ModelHandler
          .create(
            m,
            "de.windelknecht.stup"
          ) ! ModelHandler.Read(id)

        receiveN(1, 5 seconds)(0)
        verify(m).read(id)
      }

      "return a ReadFailure" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString

        when(m.read(id)).thenReturn(None)

        ModelHandler
          .create(
            m,
            "de.windelknecht.stup"
          ) ! ModelHandler.Read(id)

        val cs = receiveN(1, 5 seconds)(0)
        cs.isInstanceOf[ReadFailure] should be(right = true)
      }

      "return correct ReadFailure(id, ...)" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString

        when(m.read(id)).thenReturn(None)

        ModelHandler
          .create(
            m,
            "de.windelknecht.stup"
          ) ! ModelHandler.Read(id)

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[ReadFailure]
        cs.id should be(id)
      }

      "return correct ReadFailure(..., err)" in {
        val m = mock[Dao]
        val id =  UUID.randomUUID().toString

        when(m.read(id)).thenReturn(None)

        ModelHandler
          .create(
            m,
            "de.windelknecht.stup"
          ) ! ModelHandler.Read(id)

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[ReadFailure]
        cs.err should be(s"could not read entity with id $id")
      }
    }
  }

  "the handler class gets fired with 'ReadMore' messages" when {
    "reading a special entity class type" should {
      "call the dao" in {
        val (dao, actor, _, _, _) = createReadMoreMock()

        actor ! ModelHandler.ReadMore(Select.mapByType[mhs_entityMock_simple1])

        receiveN(1, 50 seconds)(0)
        verify(dao).read()
      }

      "return ReadMoreSuccess(result: List[StupModel[_]])" in {
        val (_, actor, _, _, _) = createReadMoreMock()

        actor ! ModelHandler.ReadMore(Select.mapByType[mhs_entityMock_simple1])

        val cs = receiveN(1, 5 seconds)(0)
        cs.isInstanceOf[ReadMoreSuccess] should be(right = true)
      }

      "return ReadMoreSuccess(result(0): mhs_modelMock_simple1)" in {
        val (_, actor, _, _, _) = createReadMoreMock()

        actor ! ModelHandler.ReadMore(Select.mapByType[mhs_entityMock_simple1])

        val cs = receiveN(1, 5 seconds)(0).asInstanceOf[ReadMoreSuccess]
        cs.select.isInstanceOf[ModelListRes] should be(right = true)
      }
    }

    "reading all entities" should {
      "call the dao" in {
        val dao = mock[Dao]

        when(dao.read()).thenReturn(List.empty)

        ModelHandler
          .create(
            dao,
            "de.windelknecht.stup"
          ) ! ModelHandler.ReadMore(Select.mapAll)

        receiveN(1, 5 seconds)(0)
        verify(dao).read()
      }
    }
  }

  "the handler class gets fired with 'Delete' messages" when {
    "the entity exists" should {
      "call the dao" in {
        val dao = mock[Dao]
        val id =  UUID.randomUUID().toString

        ModelHandler
          .create(
            dao,
            "de.windelknecht.stup"
          ) ! ModelHandler.Delete(id)

        receiveN(1, 5 seconds)(0)
        verify(dao).delete(id)
      }
    }
  }

  private def createReadMoreMock(): (Dao, ActorRef, String, String, mhs_entityMock_simple1) = {
    val m = mock[Dao]
    val id1 =  UUID.randomUUID().toString
    val ent1 = mhs_entityMock_simple1(id1)
    val id2 =  UUID.randomUUID().toString
    val ent2 = mhs_entityMock_simple1(id2)

    when(m.read()).thenReturn(List(ent1, ent2))
    when(m.read(id1)).thenReturn(Some(ent1))
    when(m.read(id2)).thenReturn(Some(ent2))

    val actor = ModelHandler
      .create(
        m,
        "de.windelknecht.stup"
      )

    (m, actor, id1, id2, ent1)
  }
}
