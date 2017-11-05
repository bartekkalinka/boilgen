package pl.bka.matchers

import scala.meta._

case class ClassMatcher(className: String, fields: Seq[Term.Param])

object ClassMatcher {
  def parseClassOnly(input: String): Either[String, ClassMatcher] = {
    val tree = input.parse[Source].get
    tree match {
      case source"..$stats" =>
        stats.head match {
          case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
            Right(ClassMatcher(tname.syntax, paramss.flatten))
          case _ =>
            Left("not a class")
        }
    }
  }

  def parseClassWithPackageAndImports(input: String): Either[String, ClassMatcher] = {
    val tree = input.parse[Source].get
    val source"..$stats" = tree
    val q"package $eref { ..$packageStats }" = stats.head
    packageStats.collect {
      case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
        ClassMatcher(tname.syntax, paramss.flatten)
    }.headOption.toRight("no class")
  }
}

