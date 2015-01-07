package de.windelknecht.stup.utils.coding.mvc

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import de.windelknecht.stup.utils.coding.StupMessages
import de.windelknecht.stup.utils.coding.akka.ActorHeHyphenMan
import de.windelknecht.stup.utils.coding.mvc.ModelHandler._
import de.windelknecht.stup.utils.coding.mvc.Select.SelectFn

import scala.concurrent.Await
import scala.concurrent.duration._

object ModelHandlerFrontend {
  // fields
  private var _actorSystem = ActorSystem("ModelHandlerFrontend")

  /**
   * Create a model handler actor.
   */
  private[ModelHandlerFrontend] def create(
    dao: Dao,
    nameSpaceForModelSearch: String
    ): ActorRef = {
    _actorSystem.synchronized {
      if (_actorSystem.isTerminated)
        _actorSystem = ActorSystem("ModelHandlerFrontend")

      ActorHeHyphenMan.create(classOf[ModelHandler], dao, nameSpaceForModelSearch)(_actorSystem)
    }
  }

}

class ModelHandlerFrontend(
  dao: Dao,
  nameSpaceForModelSearch: String,
  timeoutInSeconds: Int = 10
  ) {
  implicit val actorTimeout = Timeout(timeoutInSeconds seconds)
  implicit val futureTimeout = timeoutInSeconds seconds

  // fields
  private val _mhActor: ActorRef = ModelHandlerFrontend.create(dao, nameSpaceForModelSearch)

  /**
   * Create a model.
   */
  def create(
    className: String
    ): smhCreateRes = {
    try {
      Await.result(
        (_mhActor ? Create(className)).mapTo[smhCreateRes],
        futureTimeout
      )
    } catch {
      case e: Exception => CreateFailure(s"error while creating '$className' (${e.getMessage}})")
    }
  }

  /**
   * Delete the given model.
   */
  def delete(
    id: String
    ): smhDeleteRes = {
    _mhActor ! Delete(id)
    try {
      Await.result(
        (_mhActor ? Delete(id)).mapTo[smhDeleteRes],
        futureTimeout
      )
    } catch {
      case e: Exception => DeleteFailure(id, s"error while deleting '$id' (${e.getMessage}})")
    }
  }

  /**
   * Read the given id.
   */
  def read(
    id: String
    ): smhReadRes = {
    try {
      Await.result(
        (_mhActor ? Read(id)).mapTo[smhReadRes],
        futureTimeout
      )
    } catch {
      case e: Exception => ReadFailure(id, s"error while reading '$id' (${e.getMessage}})")
    }
  }

  /**
   * Read this zeuch.
   */
  def readMode(
    select: SelectFn
    ): smhReadMoreRes = {
    _mhActor ! ReadMore(select)
    try {
      Await.result(
        (_mhActor ? ReadMore(select)).mapTo[smhReadMoreRes],
        futureTimeout
      )
    } catch {
      case e: Exception => ReadMoreFailure(s"error while read more (${e.getMessage}})")
    }
  }

  /**
   * Shutdown this wrapper.
   */
  def shutdown(): Unit = {
    _mhActor ! StupMessages.Shutdown
  }
}
