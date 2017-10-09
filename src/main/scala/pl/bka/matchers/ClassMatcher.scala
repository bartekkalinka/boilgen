package pl.bka.matchers

import scala.meta._

case class ClassMatcher(tname: Type.Name, fields: Seq[Term.Param])

object ClassMatcher {
  def parseClassOnly(input: String): Either[String, ClassMatcher] = {
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

  def parseClassWithPackageAndImports(input: String): Either[String, ClassMatcher] = {
    val tree = input.parse[Source].get
    val source"..$stats" = tree
    val q"package $eref { ..$packageStats }" = stats.head
    packageStats.collect {
      case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
        Some(ClassMatcher(tname, paramss.flatten))
      case q"import ..$importersnel" =>
        None
      case _ =>
        None
    }.flatten.headOption.toRight("no class")
  }
}

