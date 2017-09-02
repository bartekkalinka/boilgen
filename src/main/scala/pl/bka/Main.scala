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

  val tree = input.parse[Source].get
  tree match {
    case source"..$stats" =>
      stats.head match {
        case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
          val classDefns = paramss.flatten.map { param =>
            val caseClassName = Type.Name(tname + ucFirst(param.name.syntax))
            val typeName = param.decltpe.map(d => d.syntax).getOrElse("String").parse[Type.Arg].get
            q"case class $caseClassName(value: $typeName) extends AnyVal"
          }
          val newSource = source"..$classDefns"
          println(newSource.syntax)
        case _ =>
          println("not a class")
      }
  }
}
