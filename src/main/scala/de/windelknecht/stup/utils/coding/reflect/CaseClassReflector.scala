package de.windelknecht.stup.utils.coding.reflect

import scala.reflect.ClassTag
import scala.reflect.runtime.{currentMirror => cm, universe => ru}

object CaseClassReflector {
  /**
   * Reflect the given class and return default ctor and args - ready to instantiate.
   */
  def reflectApply(clz: Class[_]): (ru.InstanceMirror, ru.MethodSymbol, ru.Type) = {
    val clazz = cm.classSymbol(clz)

    if (!clazz.isCaseClass)
      throw new IllegalArgumentException(s"'${clz.getName}' is not a case class")

    val mod = clazz.companion.asModule
    val im = cm.reflect(cm.reflectModule(mod).instance)
    val ts = im.symbol.typeSignature
    val mApply = ts.member(ru.TermName("apply")).asMethod

    (im, mApply, ts)
  }

  /**
   * Reflected apply parameter names and a instance mirror to create such a return.
   */
  def reflectApplyAndArgNames(clz: Class[_]): (ru.InstanceMirror, ru.MethodSymbol, List[String]) = {
    val (im, mApply, _) = reflectApply(clz)
    val syms = mApply.paramLists.flatten.map(_.name.toString)

    (im, mApply, syms)
  }

  /**
   * Reflect object values (from apply method).
   */
  def reflectApplyValues[T](
    obj: T
    )(implicit classTag: ClassTag[T]): List[(String, Any)] = {
    val (instanceMirror, allObjectMembers) = ObjectReflector.reflectedMembersFromObject(obj)
    val (_, _, applyArgNames) = CaseClassReflector.reflectApplyAndArgNames(obj.getClass)

    applyArgNames
      .map { i => allObjectMembers.filter(_.isMethod).find(_.name.decodedName.toString == i) }  // find all apply arg method symbols
      .collect { case Some(x) => x }                                                            // filter out None
      .map { i => instanceMirror.reflectField(i.asTerm) }                                       // get field
      .map { i => (i.symbol.name.toString.trim, i.get) }                                        // build a map: (FieldName, FieldValue)
      .toList
  }
}
