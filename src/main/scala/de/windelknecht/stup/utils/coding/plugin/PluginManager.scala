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

package de.windelknecht.stup.utils.coding.plugin

import akka.actor.{Props, ActorSystem, Actor}
import de.windelknecht.stup.utils.coding.plugin.Plugin.{PluginDescrNotLoaded, PluginDescrLoaded, OnPluginDescrInfo}
import de.windelknecht.stup.utils.coding.plugin.PluginManager._
import de.windelknecht.stup.utils.coding.reactive.Notify
import de.windelknecht.stup.utils.coding.reactive.Notify._
import org.apache.commons.vfs2.{VFS, FileObject}

import scala.collection.mutable

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 23.07.14
 * Time: 12:44
 *
 */
object PluginManager {
  trait pmEvent extends NotifyEvent
  case object OnPluginChanged extends pmEvent

  trait pmMsg
  trait pmReq extends pmMsg
  trait pmRes extends pmMsg

  case class PluginLoaded   (plugin: Plugin)              extends pmRes
  case class PluginNotLoaded(plugin: Plugin, err: String) extends pmRes

  case class AddPlugin(file: String)   extends pmReq
  case class AddPluginDir(dir: String) extends pmReq

  /**
   * Create a plugin manager.
   */
  def apply(
    notify: Option[NotifyRx] = None
    )(implicit actorSystem: ActorSystem) = actorSystem.actorOf(Props(classOf[PluginManager], notify))
}

class PluginManager(
  notify: Option[NotifyRx]
  )
  extends Actor
  with Notify {
  // fields
  private val _plugins = new mutable.HashMap[FileObject, Plugin]()

  // ctor
  registerNotify(notify)

  /**
   * Actor receive method
   */
  override def receive = notifyReceive orElse thisReceive

  /**
   * Our receive method
   */
  protected def thisReceive: Receive = {
    case AddPlugin(file)   => addPlugin(VFS.getManager.resolveFile(file))
    case AddPluginDir(dir) => addPluginDir(VFS.getManager.resolveFile(dir))

    case _ =>
  }

  /**
   * Add on single plugin
   *
   * @param file jar file to add as a plugin
   */
  private def addPlugin(
    file: FileObject
    ) {
    _plugins synchronized {
      _plugins += (file -> new Plugin(file, notify = Some({
        case (OnPluginDescrInfo, PluginDescrLoaded,         plugin: Plugin) +++ _ => fireNotify(OnPluginChanged, PluginLoaded(plugin))
        case (OnPluginDescrInfo, PluginDescrNotLoaded(err), plugin: Plugin) +++ _ => fireNotify(OnPluginChanged, PluginNotLoaded(plugin, err))
      })))
    }
  }

  /**
   * Add all plugin with this dir and (TODO: install a file watcher.)
   *
   * @param dir containing jar files
   */
  private def addPluginDir(
    dir: FileObject
    ) {
    dir
      .getChildren
      .filter(_.getName.getExtension == "jar")
      .foreach(addPlugin)
  }
}
