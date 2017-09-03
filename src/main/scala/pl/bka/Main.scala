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

  case class ClassDefnWithName(name: Type.Name, defn: Defn.Class)

  def anyValClassForField(mainClassName: Type.Name, field: Term.Param): ClassDefnWithName = {
    val caseClassName = Type.Name(mainClassName + ucFirst(field.name.syntax))
    val typeName = field.decltpe.get
    ClassDefnWithName(caseClassName, q"case class $caseClassName(value: $typeName) extends AnyVal")
  }

  val tree = input.parse[Source].get
  tree match {
    case source"..$stats" =>
      stats.head match {
        case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
          val anyValClassDefns = paramss.flatten.map(anyValClassForField(tname, _))
          val newSource = source"..${anyValClassDefns.map(_.defn)}"
          println(newSource.syntax)
        case _ =>
          println("not a class")
      }
  }
}
