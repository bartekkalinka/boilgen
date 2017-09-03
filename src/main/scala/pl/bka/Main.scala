package pl.bka

import scala.meta._

object Main extends App {
  val input =
    """
      |case class Transaction(
      |  id: UUID,
      |  value: Long,
      |  createDate: Instant,
      |  modifiedDate: Instant,
      |  cardNumber: String,
      |  description: String
      |)
    """.stripMargin

  def ucFirst(str: String): String = Character.toUpperCase(str.charAt(0)) + str.substring(1)

  case class ClassDefnWithName(name: Type.Name, defn: Defn.Class, originalFieldName: Term.Param.Name)

  def anyValClassForField(mainClassName: Type.Name, field: Term.Param): ClassDefnWithName = {
    val caseClassName = Type.Name(mainClassName + ucFirst(field.name.syntax))
    val typeName = field.decltpe.getOrElse(Type.Name("String"))
    val anyValClass = q"case class $caseClassName(value: $typeName) extends AnyVal"
    ClassDefnWithName(caseClassName, anyValClass, field.name)
  }

  val tree = input.parse[Source].get
  tree match {
    case source"..$stats" =>
      stats.head match {
        case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
          val anyValClassDefns = paramss.flatten.map(anyValClassForField(tname, _))
          val anyValClassesSource = source"..${anyValClassDefns.map(_.defn)}"
          println(anyValClassesSource.syntax)
          val replacedTypesFields = anyValClassDefns.map { case ClassDefnWithName(className, _, fieldName) =>
            Term.Param(List.empty[Mod], fieldName, Some(className), None)
          }
          val replacedTypesClassSource = source"case class $tname(...${List(replacedTypesFields)})"
          println(replacedTypesClassSource.syntax)
        case _ =>
          println("not a class")
      }
  }
}
