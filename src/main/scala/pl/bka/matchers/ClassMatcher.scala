package pl.bka.matchers

import scala.meta._

case class ClassMatcher(tname: Type.Name, fields: Seq[Term.Param])

object ClassMatcher {
  def parse(input: String): Either[String, ClassMatcher] = {
    val tree = input.parse[Source].get
    tree match {
      case source"..$stats" =>
        stats.collect {
          case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
            Some(ClassMatcher(tname, paramss.flatten))
          case stat =>
            println(s"11111111111111 ${stat.syntax}")
            None
        }.flatten.headOption.toRight("no class")
    }
  }
}

