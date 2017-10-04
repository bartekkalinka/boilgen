package pl.bka

import pl.bka.FieldsToAnyValCaseClasses.FieldWithAnyVal

import scala.meta._

object FieldsToSlickTable {
  case class SlickTableOutput(slickTable: String)

  def generate(mainClassName: String, fields: Seq[FieldWithAnyVal]): SlickTableOutput = {
    val typeName = Type.Name(mainClassName)
    val tableClassName = Type.Name(s"${typeName}s")
    val dbTableName = TextUtils.camelToUnderscores(TextUtils.lcFirst(tableClassName.syntax))
    val initArgs = List(List(q"tag", Lit.String(dbTableName)))
    val outputSource =
      source"""
              case class $tableClassName(tag: BaseTable.Tag) extends BaseTable[$typeName](...$initArgs) {
                import profile.api._
              }
        """
    SlickTableOutput(outputSource.syntax)
  }
}

