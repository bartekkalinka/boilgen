package pl.bka.generators

import pl.bka.matchers.ClassMatcher
import pl.bka.utils.TextUtils

import scala.meta._

object AnyValGen {

  case class FieldWithAnyVal(fieldName: String, anyValType: String)

  case class AnyValCaseClassesOutput(anyValClasses: String, replacedTypesClass: String, mainClassName: String, fields: Seq[FieldWithAnyVal])

  def generate(matcher: ClassMatcher): AnyValCaseClassesOutput = {
    val (anyValClassesSource, fields) = generateAnyValClassDefns(matcher)
    val replacedTypesClassSource = generateReplacedTypesClass(matcher.tname, fields)
    AnyValCaseClassesOutput(
      anyValClassesSource, replacedTypesClassSource, matcher.tname.syntax, fields
    )
  }

  private def generateAnyValClassDefns(matcher: ClassMatcher): (String, Seq[FieldWithAnyVal]) = {
    val (anyValClassDefns, fields) = matcher.fields.map(anyValClassForField(matcher.tname, _)).unzip
    val anyValClassesSource = source"..${anyValClassDefns.toList}"
    (anyValClassesSource.syntax, fields)
  }

  private def generateReplacedTypesClass(tname: Type.Name, fields: Seq[FieldWithAnyVal]): String = {
    val replacedTypesFields = fields.map { case FieldWithAnyVal(fieldName, anyValType) =>
      Term.Param(List.empty[Mod], q"fieldName", Some(Type.Name(anyValType)), None)
    }
    val replacedTypesClassSource = source"case class $tname(...${List(replacedTypesFields.toList)})"
    reformatOutputClass(replacedTypesClassSource.syntax)
  }

  def reformatOutputClass(classSource: String): String = {
    val tokens = classSource.tokenize.get
    val builder = new StringBuilder()
    tokens.foreach { token =>
      if(token.is[Token.LeftParen]) {
        builder.append(token.syntax)
        builder.append("\n  ")
      } else if(token.is[Token.Comma]) {
        builder.append(token.syntax)
        builder.append("\n ")
      } else if(token.is[Token.RightParen]) {
        builder.append("\n")
        builder.append(token.syntax)
      } else {
        builder.append(token.syntax)
      }
    }
    builder.mkString
  }

  private def anyValClassForField(mainClassName: Type.Name, field: Term.Param): (Defn.Class, FieldWithAnyVal) = {
    val caseClassName = Type.Name(mainClassName + TextUtils.ucFirst(field.name.syntax))
    val typeName = field.decltpe.getOrElse(Type.Name("String"))
    val anyValClass = q"case class $caseClassName(value: $typeName) extends AnyVal"
    (
      anyValClass,
      FieldWithAnyVal(field.name.syntax, caseClassName.value)
    )
  }
}

