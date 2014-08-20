/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Heiko Blobner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.windelknecht.stup.utils.coding.akka

import java.util.UUID

import akka.actor.{Props, ActorRef, ActorSystem}
import de.windelknecht.stup.utils.coding.akka.Reaper.WatchMe

import scala.reflect.ClassTag

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 08.07.14
 * Time: 15:58
 *
 */
object ActorHeHyphenMan {
  // fields
  private val _asToReaper = new collection.mutable.HashMap[ActorSystem, ActorRef]()

  /**
   * Create a actor ref and registers on the shutdown watcher.
   *
   * @param args is passed to newly created actor
   * @param classTag is the type information
   * @param actorSystem the actor is created within this system
   * @tparam C is the type information
   * @return ActorRef of our new actor
   */
  def apply[C](
    args: Any*
    )(implicit classTag: ClassTag[C], actorSystem: ActorSystem): ActorRef = ActorHeHyphenMan.create(classTag.runtimeClass, args:_*)

  /**
   * Create a actor ref and registers on the shutdown watcher.
   *
   * @param args is passed to newly created actor
   * @param actorSystem the actor is created within this system
   * @return ActorRef of our new actor
   */
  def create(
    className: String,
    args: Any*
    )(implicit actorSystem: ActorSystem): ActorRef = ActorHeHyphenMan.create(Class.forName(className), args:_*)

  /**
   * Create a actor ref and registers on the shutdown watcher.
   *
   * @param clazz is the actor class
   * @param args is passed to newly created actor
   * @param actorSystem the actor is created within this system
   * @return ActorRef of our new actor
   */
  def create(
    clazz: Class[_],
    args: Any*
    )(implicit actorSystem: ActorSystem): ActorRef = watchShutdown(actorSystem.actorOf(Props(clazz, args:_*), s"${clazz.getName}_${UUID.randomUUID()}"))

  /**
   * Set actor on watch list. If no actor is alive anymore, the actor system will be shut down.
   */
  def watchShutdown(
    actor: ActorRef
    )(implicit actorSystem: ActorSystem) = {
    _asToReaper synchronized {
      val reaper = _asToReaper.getOrElseUpdate(actorSystem, actorSystem.actorOf(Props(classOf[ProductionReaper])))

      reaper ! WatchMe(actor)
      actor
    }
  }
}
