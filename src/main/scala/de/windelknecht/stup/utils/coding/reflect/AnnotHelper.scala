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

package de.windelknecht.stup.utils.coding.reflect

import scala.annotation.StaticAnnotation
import scala.reflect.runtime.{universe => ru}

object AnnotHelper {
  /**
   * Comfortable class for working with a list of annotations.
   *
   * @param annots is a list of annotation
   */
  implicit class AnnotListHelper(
    annots: List[ru.Annotation]
    ) {
    /**
     * Returns true if the list contains the given annotation
     *
     * @param ta type tp find
     * @tparam A type to return
     * @return option of an instantiated annotation
     */
    def containsAnnot[A]()(implicit ta: ru.TypeTag[A]) = AnnotHelper.contains(annots)

    /**
     * Find annotation of the given type within the list.
     *
     * @param ta type tp find
     * @tparam A type to return
     * @return option of the annotation
     */
    def findAnnot[A]()(implicit ta: ru.TypeTag[A]) = AnnotHelper.find(annots)

    /**
     * Find annotation of the given type within the list and instantiate.
     *
     * @param ta type tp find
     * @tparam A type to return
     * @return option of an instantiated annotation
     */
    def getAnnot[A]()(implicit ta: ru.TypeTag[A]) = create[A]()
  }

  /**
   * Returns true if the list contains the given annotation
   *
   * @param annots list of annotations
   * @param ta type tp find
   * @tparam A type to return
   * @return option of an instantiated annotation
   */
  def contains[A](
    annots: List[ru.Annotation]
    )(implicit ta: ru.TypeTag[A]) = {
    find[A](annots) match {
      case Some(x) => true
      case None    => false
    }
  }

  /**
   * Find annotation of the given type within the list and instantiate.
   *
   * @param annots list of annotations
   * @param ta type tp find
   * @tparam A type to return
   * @return option of an instantiated annotation
   */
  def create[A](
    annots: List[ru.Annotation]
    )(implicit ta: ru.TypeTag[A]): Option[A] = {
    find[A](annots) match {
      case Some(x) => Some(instantiate[A](x))
      case None    => None
    }
  }

  /**
   * Find annotation of the given type within the list.
   *
   * @param annots list of annotations
   * @param ta type tp find
   * @tparam A type to return
   * @return option of the annotation
   */
  def find[A](annots: List[ru.Annotation])(implicit ta: ru.TypeTag[A]): Option[ru.Annotation] = annots.find(a=> a.tree.tpe == ta.tpe)

  /**
   * Get the class file annotation of the given class type.
   * An exception is thrown if the given type is not annotated.
   *
   * @param tpe type information of the class
   * @param ta type information of the annotation
   * @tparam A is the type of the annotation to find
   * @return instance of the annotation
   */
  def get[A <: StaticAnnotation](tpe: ru.Type)(implicit ta: ru.TypeTag[A]): A = instantiate(tpe.typeSymbol.asClass.annotations.find(a => a.tree.tpe == ta.tpe).get)

  /**
   * Get the class file annotation of the given class type.
   * An exception is thrown if the given type is not annotated.
   *
   * @param ta type information of the annotation
   * @param tt type information of the class
   * @tparam A is the type of the annotation to find
   * @tparam V is the type of the annotated class
   * @return instance of the annotation
   */
  def get[A <: StaticAnnotation, V]()(implicit ta: ru.TypeTag[A], tt: ru.TypeTag[V]): A = get[A](tt.tpe)

  /**
   * Instantiate the given annotation
   *
   * @param annot the AnnotationInfo
   * @param ta annotation type
   * @tparam A generic type
   * @return instantiated
   */
  def instantiate[A](
    annot: ru.Annotation
    )(implicit ta: ru.TypeTag[A]): A = {
    import scala.reflect.runtime.universe._ // sorgt dafür, dass die haessliche 'abstract type pattern reflect.runtime.universe.AssignOrNamedArg is unchecked since it is eliminated by erasure' wegkommt

    require(annot.tree.tpe == ta.tpe, "passed argument does not match generic annotation type")

    val annotType = ta.tpe                                                                // get the expected annotation type to match

    val args = annot
      .tree.children.tail                                                                 // retrieve the args. These are returned as a list of Tree.
      .collect{                                                                           // convert list of Tree to list of argument values
        case ru.Literal(ru.Constant(m)) => m
      }

    val runtimeMirror = ru.runtimeMirror(this.getClass.getClassLoader)                    // get runtimeMirror
    val classSymbol = annotType.typeSymbol.asClass                                        // get ClassSymbol for the annotation class
    val classMirror = runtimeMirror.reflectClass(classSymbol)                             // get classMirror for the class
    val constructorMethodSymbol = annotType.decl(ru.termNames.CONSTRUCTOR).asMethod       // get MethodSymbol for the constructor method
    val constructorMethodMirror = classMirror.reflectConstructor(constructorMethodSymbol) // get constructorMethodMirror for the method
    val instance = constructorMethodMirror(args: _*).asInstanceOf[A]                      // use the constructor to instance the annotation class.  Pass in the arguments.

    instance
  }
}
