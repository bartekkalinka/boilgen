package pl.bka.generators

import pl.bka.generators.AnyValGen.FieldWithAnyVal
import pl.bka.utils.TextUtils

import scala.meta._

object SlickTableGen {
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
                ..${fields.map(dbField).toList}
                ${starDef(mainClassName, fields)}
              }
        """
    SlickTableOutput(outputSource.syntax)
  }

  private def dbField(field: FieldWithAnyVal): Defn.Def = {
    val tpe = t"${Type.Name(field.anyValType)}"
    val columnName = TextUtils.camelToUnderscores(field.fieldName)
    q"def ${Term.Name(field.fieldName)}: Rep[$tpe] = column[$tpe](${Lit.String(columnName)})"
  }

  private def starDef(mainClassName: String, fields: Seq[FieldWithAnyVal]): Defn.Def = {
    val mainClassType = Type.Name(mainClassName)
    val mainClassTerm = Term.Name(mainClassName)
    val fieldTerms = fields.map(f => Term.Name(f.fieldName)).toList
    val tuple = q"(..$fieldTerms)"
    q"override def * : ProvenShape[$mainClassType] = $tuple <> (($mainClassTerm.apply _).tupled, $mainClassTerm.unapply)"
  }
}

