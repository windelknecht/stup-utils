package de.windelknecht.stup.utils.coding.reflect

import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}

object ObjectReflector {
  /**
   * Return all members of the given object.
   *
   * @param data is the object to discover
   * @param classTag implicit classtag (used for reflection)
   * @tparam T type parameter
   * @return class members
   */
  def reflectedMembersFromObject[T](
    data: T
    )(implicit classTag: ClassTag[T]): (ru.InstanceMirror, ru.MemberScope) = {
    val typeMirror = ru.runtimeMirror(data.getClass.getClassLoader)
    val instanceMirror = typeMirror.reflect(data)
    val members = instanceMirror.symbol.typeSignature.members

    (instanceMirror, members)
  }

  /**
   * Get type infor for the method given by name.
   */
  def reflectTypeByMemberName(
    obj: Any,
    memberName: String
    ): Option[ru.Type] = {
    val (instanceMirror, allObjectMembers) = ObjectReflector.reflectedMembersFromObject(obj)
    allObjectMembers
      .filter(_.isMethod)
      .find(_.name.decodedName.toString == memberName) match {
      case Some(x) => Some(instanceMirror.reflectField(x.asTerm).symbol.info)
      case None => None
    }
  }
}
