package de.windelknecht.stup.utils.coding.mvc

import java.util.UUID

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.runtime.{currentMirror => cm, universe => ru}

/**
 * Base trait of all entities. They all must implement an id method to return their unique
 * entity id.
 */
object Entity {
  /**
   * Private usage.
   */
  private val _reflApplyCache = new mutable.HashMap[String, (ru.InstanceMirror, ru.MethodSymbol, List[Any])]()
  private val _reflApplyArgNameCache = new mutable.HashMap[Class[_], (ru.InstanceMirror, ru.MethodSymbol, List[String])]()
  private val _EntityClazz = classOf[Entity]
  private val _optionRegex = """(.*)Option\[(.*)\]""".r

  /**
   * Create an entity - via cached reflection.
   */
  def create(className: String): Entity = create(Class.forName(className))

  /**
   * Create an entity - via cached reflection.
   */
  def create[T <: Entity]()(implicit classTag: ClassTag[T]): T = create(classTag.runtimeClass).asInstanceOf[T]

  /**
   * Create an entity - via cached reflection.
   */
  def create(clazz: Class[_]): Entity = {
    val className = clazz.getName

    if(!_EntityClazz.isAssignableFrom(clazz))
      throw new IllegalArgumentException(s"You want me to create a entity, but '${_EntityClazz.getName}' is not the super class of your wish: '$className'")

    val (im, mApply, args) = _reflApplyCache.synchronized { _reflApplyCache.getOrElseUpdate(className, reflectApplyAndDefaultArgs(clazz)) }
    im
      .reflectMethod(mApply)(args: _*)
      .asInstanceOf[Entity]
  }

  /**
   * Return, and cache if necessary, the reflected apply parameter names and a instance mirror to create such a entity type.
   */
  private[mvc] def getReflectedApplyWArgNames[T](
    )(implicit classTag: ClassTag[T]): (ru.InstanceMirror, ru.MethodSymbol, List[String]) = {
    _reflApplyArgNameCache.synchronized {
      _reflApplyArgNameCache.getOrElseUpdate(classTag.runtimeClass, reflectApplyAndArgNames[T]())
    }
  }

  /**
   * Reflect the given class and return default ctor and args - ready to instantiate.
   */
  protected def reflectApply(
    clz: Class[_]
    ) = {
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
  private def reflectApplyAndArgNames[T]()(implicit classTag: ClassTag[T]): (ru.InstanceMirror, ru.MethodSymbol, List[String]) = {
    val (im, mApply, _) = reflectApply(classTag.runtimeClass)
    val syms = mApply.paramLists.flatten.map(_.name.toString)

    (im, mApply, syms)
  }

  /**
   * Reflect the given class and return default ctor and args - ready to instantiate.
   */
  protected def reflectApplyAndDefaultArgs(
    clz: Class[_]
    ) = {
    val (im, mApply, ts) = reflectApply(clz)
    val syms = mApply.paramLists.flatten
    val args = syms.zipWithIndex.map { case (p, i) =>
      val mDef = ts.member(ru.TermName(s"apply$$default$$${i + 1}"))

      if(mDef != ru.NoSymbol) {
        im.reflectMethod(mDef.asMethod)()
      } else {
        p.typeSignature.toString match {
          case "Char"                => 0.toChar
          case "Character"           => 0.toChar
          case "java.lang.Character" => 0.toChar
          case "scala.Char"          => 0.toChar

          case "Boolean"           => false
          case "java.lang.Boolean" => false
          case "scala.Boolean"     => false

          case "Byte"           => 0.toByte
          case "java.lang.Byte" => 0.toByte
          case "scala.Byte"     => 0.toByte

          case "Double"           => 0f.toDouble
          case "java.lang.Double" => 0f.toDouble
          case "scala.Double"     => 0f.toDouble

          case "Float"           => 0f
          case "java.lang.Float" => 0f
          case "scala.Float"     => 0f

          case "Int"               => 0
          case "Integer"           => 0
          case "java.lang.Integer" => 0
          case "scala.Int"         => 0

          case "Long"           => 0.toLong
          case "java.lang.Long" => 0.toLong
          case "scala.Long"     => 0.toLong

          case "Short"           => 0.toShort
          case "java.lang.Short" => 0.toShort
          case "scala.Short"     => 0.toShort

          case "String"           => ""
          case "java.lang.String" => ""

          case _optionRegex(_, _)        => None
          case _optionRegex("scala.", _) => None

          case "java.util.UUID" => UUID.randomUUID()

          case t => throw new IllegalArgumentException(s"arg '$p' of class ${clz.getName} has a complex type (${p.typeSignature}) and has no default, so please correct")
        }
      }
    }
    (im, mApply, args)
  }
}

trait Entity {
  def id: UUID
}
