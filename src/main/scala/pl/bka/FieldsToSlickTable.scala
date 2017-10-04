package pl.bka

import scala.meta._

object FieldsToSlickTable {
  case class SlickTableOutput(slickTable: String)

  def generate(input: String): Either[String, SlickTableOutput] = {
    val tree = input.parse[Source].get
    tree match {
      case source"..$stats" =>
        stats.head match {
          case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
            val tableClassName = Type.Name(s"${tname}s")
            val dbTableName = TextUtils.camelToUnderscores(TextUtils.lcFirst(tableClassName.syntax))
            val initsArgs = List(List(q"tag", Lit.String(dbTableName)))
            val outputSource =
              source"""
                      case class $tableClassName(tag: BaseTable.Tag) extends BaseTable[$tname](...$initsArgs) {
                        import profile.api._
                      }
                """
            Right(SlickTableOutput(outputSource.syntax))
          case _ =>
            Left("not a class")
        }
    }
  }
}

