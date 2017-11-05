package pl.bka.generators

import pl.bka.matchers.ClassMatcher
import pl.bka.syntax.Field
import pl.bka.utils.TextUtils

import scala.meta._

object AnyValGen {

  case class AnyValCaseClassesOutput(anyValClasses: String, replacedTypesClass: String, mainClassName: String, fields: Seq[Field])

  def generate(matcher: ClassMatcher): AnyValCaseClassesOutput = {
    val (anyValClassesSource, anyValFields) = generateAnyValClassDefns(matcher)
    val replacedTypesClassSource = generateReplacedTypesClass(matcher.className, anyValFields)
    AnyValCaseClassesOutput(
      anyValClassesSource, replacedTypesClassSource, matcher.className, anyValFields
    )
  }

  private def generateAnyValClassDefns(matcher: ClassMatcher): (String, Seq[Field]) = {
    val (anyValClassDefns, fields) = matcher.fields.map(anyValClassForField(matcher.className, _)).unzip
    val anyValClassesSource = source"..${anyValClassDefns.toList}"
    (anyValClassesSource.syntax, fields)
  }

  private def generateReplacedTypesClass(mainClassName: String, anyValFields: Seq[Field]): String = {
    val replacedTypesFields = anyValFields.map { case Field(anyValFieldName, anyValType) =>
      val termParamName = Term.Name(anyValFieldName)
      Term.Param(List.empty[Mod], termParamName, Some(Type.Name(anyValType)), None)
    }
    val tname = Type.Name(mainClassName)
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

  private def anyValClassForField(mainClassName: String, field: Field): (Defn.Class, Field) = {
    val caseClassName = Type.Name(mainClassName + TextUtils.ucFirst(field.name))
    val typeName = Type.Name(field.tpe)
    val anyValClass = q"case class $caseClassName(value: $typeName) extends AnyVal"
    (
      anyValClass,
      Field(field.name, caseClassName.value)
    )
  }
}

