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

import java.net.URLClassLoader
import java.util.UUID

import de.windelknecht.stup.utils.coding.plugin.Plugin._
import de.windelknecht.stup.utils.coding.reactive.Notify
import de.windelknecht.stup.utils.coding.reactive.Notify.{NotifyRx, NotifyEvent}
import de.windelknecht.stup.utils.io.pack.archive.UnArchiver
import org.apache.commons.vfs2.{VFS, FileObject}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.runtime.{universe => ru}

/**
 * Created by Me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 23.07.14
 * Time: 16:10
 *
 */
object Plugin {
  trait phEvent extends NotifyEvent
  case object OnPluginDescrInfo extends phEvent

  trait phMsg
  case object PluginDescrLoaded                 extends phMsg
  case class  PluginDescrNotLoaded(err: String) extends phMsg
}

class Plugin(
  plugin: FileObject,
  notify: Option[NotifyRx] = None
  )
  extends Notify {
  case class FileToClassMap(file: FileObject, clazz: Class[_])
  case class ExtractedJar(tempDir: FileObject, items: List[FileToClassMap])

  // fields
  private val _classLoader = createClassLoader(plugin)
  private val _extractedJar = extractJar(plugin)
  private val _pluginDescr = findPluginDescr()

  // ctor
  registerNotify(notify)

  /**
   * Return the plugin descriptor object.
   *
   * @return siehe oben
   */
  def pluginDescr: Option[PluginDescr] = Await.result(_pluginDescr, 30 seconds)

  /**
   * Return all subclass in this plugin
   *
   * @param superClass is the super class to find derivations
   * @return list of sub classes
   */
  def findSubClasses(
    superClass: Class[_]
    ): List[Class[_]] = findClassesByType(superClass)

  protected def classLoader = Await.result(_classLoader, 30 seconds)
  protected def extractedJar = Await.result(_extractedJar, 30 seconds)

  /**
   * Create a class loader - as future
   *
   * @param file is the jar file
   * @return future with class loader as result
   */
  private def createClassLoader(
    file: FileObject
    ): Future[ClassLoader] = {
    Future {
      URLClassLoader.newInstance(
        Array(file.getURL),
        getClass.getClassLoader
      )
    }
  }

  /**
   * Extract given jar into a temp dir.
   *
   * @param file is the jar to extract
   * @return future with extracted objects as jar
   */
  private def extractJar(
    file: FileObject
    ): Future[Option[ExtractedJar]] = {
    Future {
      val un = new UnArchiver(file,
        VFS.getManager.resolveFile(s"ram://pluginManager/${file.getName.getBaseName}/${UUID.randomUUID()}"))

      Await.result(un.run(), 10 seconds).fold(
        error => None,
        res =>
          Some(
            ExtractedJar(
              res._1,
              res
                ._2
                .filter(_.getName.getExtension == "class")
                .map { f => FileToClassMap(f, getClassFromFilename(res._1, f))}
            ))
      )
    }
  }

  /**
   * Iterate over all classes and find the one with superClass as super class.
   *
   * @param superClass results must match this class object
   * @return list of classes
   */
  private def findClassesByType(
    superClass: Class[_]
    ) = {
    extractedJar match {
      case Some(x) => findClassesType(superClass, x.items)
      case None    => List.empty
    }
  }

  /**
   * Iterate over all classes and find the one with superClass as super class.
   *
   * @param superClass results must match this class object
   * @param list list of our maps
   * @return list of classes
   */
  private def findClassesType(
    superClass: Class[_],
    list: List[FileToClassMap]
    ) = {
    list
      .filter(m => superClass.isAssignableFrom(m.clazz))
      .map(_.clazz)
  }

  /**
   * Find the plugin descriptor class within the plugin and instantiate ist.
   *
   * @return future with an plugin option
   */
  private def findPluginDescr(): Future[Option[PluginDescr]] = {
    Future {
      val plugins = findClassesByType(classOf[PluginDescr])

      try {
        if (plugins.nonEmpty) {
          val obj = instantiate[PluginDescr](plugins.head)
          fireNotify(OnPluginDescrInfo, PluginDescrLoaded)
          Some(obj)
        } else
          None
      } catch {
        case e: Exception =>
          fireNotify(OnPluginDescrInfo, PluginDescrNotLoaded(err = e.getMessage))
          None
      }
    }
  }

  /**
   * Create class from file name
   *
   * @param dir is the dir, which will be extracted from file to get the class name with namespace
   * @param file is the '.class' file
   * @return class info object
   */
  private def getClassFromFilename(
    dir: FileObject,
    file: FileObject
    ): Class[_] = {
    val clazzName = file
      .getName
      .getPath
      .replaceFirst(s"${dir.getName.getPath}/", "")
      .replaceAll(".class", "")
      .replaceAll("/", ".")

    Class.forName(clazzName, false, classLoader)
  }

  /**
   * Try to instantiate the given class.
   *
   * @param clazz is the class info object
   * @tparam T type of the object to return
   * @return instantiated object
   */
  private def instantiate[T](
    clazz: Class[_]
    ): T = {
    if(clazz.getName.endsWith("$"))
      instantiateObject(clazz)
    else
      instantiateClass(clazz)
  }

  /**
   * Return the singleton instance of this underlying class.
   *
   * @param clazz is the class info object
   * @tparam T type of the object to return
   * @return instantiated object
   */
  private def instantiateClass[T](
    clazz: Class[_]
    ): T = {
    clazz
      .newInstance()
      .asInstanceOf[T]
  }

  /**
   * Return the singleton instance of this underlying object.
   *
   * @param clazz is the class info object
   * @tparam T type of the object to return
   * @return instantiated object
   */
  private def instantiateObject[T](
    clazz: Class[_]
    ): T = {
    val runtimeMirror = ru.runtimeMirror(classLoader)
    val module = runtimeMirror.staticModule(clazz.getName)
    val obj = runtimeMirror.reflectModule(module)

    obj.instance.asInstanceOf[T]
  }
}
