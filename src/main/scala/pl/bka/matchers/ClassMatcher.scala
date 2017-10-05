package pl.bka.matchers

import scala.meta._

case class ClassMatcher(tname: Type.Name, fields: Seq[Term.Param])

object ClassMatcher {
  def parse(input: String): Either[String, ClassMatcher] = {
    val tree = input.parse[Source].get
    tree match {
      case source"..$stats" =>
        stats.head match {
          case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
            Right(ClassMatcher(tname, paramss.flatten))
          case _ =>
            Left("not a class")
        }
    }
  }
}

