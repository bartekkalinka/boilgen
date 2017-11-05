package pl.bka.generators

import pl.bka.matchers.ClassMatcher
import pl.bka.utils.TextUtils

import scala.meta._

object AnyValGen {

  case class FieldWithAnyVal(fieldName: String, anyValType: String)

  case class AnyValCaseClassesOutput(anyValClasses: String, replacedTypesClass: String, mainClassName: String, fields: Seq[FieldWithAnyVal])

  def generate(matcher: ClassMatcher): AnyValCaseClassesOutput = {
    val (anyValClassNames, anyValClassesSource, fields) = generateAnyValClassDefns(matcher)
    val replacedTypesClassSource = generateReplacedTypesClass(matcher.tname, anyValClassNames.toList)
    AnyValCaseClassesOutput(
      anyValClassesSource, replacedTypesClassSource, matcher.tname.syntax, fields
    )
  }

  private def generateAnyValClassDefns(matcher: ClassMatcher): (Seq[ClassDefnName], String, Seq[FieldWithAnyVal]) = {
    val (anyValClassDefns, anyValClassNames, fields) = matcher.fields.map(anyValClassForField(matcher.tname, _)).unzip3
    val anyValClassesSource = source"..${anyValClassDefns.toList}"
    (anyValClassNames, anyValClassesSource.syntax, fields)
  }

  private def generateReplacedTypesClass(tname: Type.Name, anyValClassNames: Seq[ClassDefnName]): String = {
    val replacedTypesFields = anyValClassNames.map { case ClassDefnName(className, fieldName) =>
      Term.Param(List.empty[Mod], fieldName, Some(className), None)
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

  private case class ClassDefnName(name: Type.Name, originalFieldName: Term.Param.Name)

  private def anyValClassForField(mainClassName: Type.Name, field: Term.Param): (Defn.Class, ClassDefnName, FieldWithAnyVal) = {
    val caseClassName = Type.Name(mainClassName + TextUtils.ucFirst(field.name.syntax))
    val typeName = field.decltpe.getOrElse(Type.Name("String"))
    val anyValClass = q"case class $caseClassName(value: $typeName) extends AnyVal"
    (
      anyValClass,
      ClassDefnName(caseClassName, field.name),
      FieldWithAnyVal(field.name.syntax, caseClassName.value)
    )
  }
}

