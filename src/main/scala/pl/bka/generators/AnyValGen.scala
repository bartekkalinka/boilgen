package pl.bka.generators

import pl.bka.matchers.ClassMatcher
import pl.bka.utils.TextUtils

import scala.meta._

object AnyValGen {

  case class FieldWithAnyVal(fieldName: String, anyValType: String)

  case class AnyValCaseClassesOutput(anyValClasses: String, replacedTypesClass: String, mainClassName: String, fields: Seq[FieldWithAnyVal])

  def generate(matcher: ClassMatcher): AnyValCaseClassesOutput = {
    val (anyValClassDefns, anyValClassesSource, fields) = generateAnyValClassDefns(matcher.tname, matcher.fields)
    AnyValCaseClassesOutput(
      anyValClassesSource, generateReplacedTypesClass(matcher.tname, anyValClassDefns.toList), matcher.tname.syntax, fields
    )
  }

  private def generateAnyValClassDefns(tname: Type.Name, inputFields: Seq[Term.Param]): (Seq[ClassDefnWithName], String, Seq[FieldWithAnyVal]) = {
    val (anyValClassDefns, fields) = inputFields.map(anyValClassForField(tname, _)).unzip
    val anyValClassesSource = source"..${anyValClassDefns.map(_.defn).toList}"
    (anyValClassDefns, anyValClassesSource.syntax, fields)
  }

  private def generateReplacedTypesClass(tname: Type.Name, anyValClassDefns: Seq[ClassDefnWithName]): String = {
    val replacedTypesFields = anyValClassDefns.map { case ClassDefnWithName(className, _, fieldName) =>
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

  private case class ClassDefnWithName(name: Type.Name, defn: Defn.Class, originalFieldName: Term.Param.Name)

  private def anyValClassForField(mainClassName: Type.Name, field: Term.Param): (ClassDefnWithName, FieldWithAnyVal) = {
    val caseClassName = Type.Name(mainClassName + TextUtils.ucFirst(field.name.syntax))
    val typeName = field.decltpe.getOrElse(Type.Name("String"))
    val anyValClass = q"case class $caseClassName(value: $typeName) extends AnyVal"
    (
      ClassDefnWithName(caseClassName, anyValClass, field.name),
      FieldWithAnyVal(field.name.syntax, caseClassName.value)
    )
  }
}

