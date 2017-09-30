package pl.bka

import scala.meta._

object FieldsToAnyValCaseClasses {

  case class AnyValCaseClassesOutput(anyValClasses: String, replacedTypesClass: String)

  def generate(input: String): Either[String, AnyValCaseClassesOutput] = {
    val tree = input.parse[Source].get
    tree match {
      case source"..$stats" =>
        stats.head match {
          case q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends $template" =>
            val (anyValClassDefns, anyValClassesSource) = generateAnyValClassDefns(tname, paramss)
            Right(AnyValCaseClassesOutput(anyValClassesSource, generateReplacedTypesClass(tname, anyValClassDefns.toList)))
          case _ =>
            Left("not a class")
        }
    }
  }

  private def generateAnyValClassDefns(tname: Type.Name, paramss: Seq[Seq[Term.Param]]): (Seq[ClassDefnWithName], String) = {
    val anyValClassDefns = paramss.flatten.map(anyValClassForField(tname, _))
    val anyValClassesSource = source"..${anyValClassDefns.map(_.defn).toList}"
    (anyValClassDefns, anyValClassesSource.syntax)
  }

  private def generateReplacedTypesClass(tname: Type.Name, anyValClassDefns: Seq[ClassDefnWithName]): String = {
    val replacedTypesFields = anyValClassDefns.map { case ClassDefnWithName(className, _, fieldName) =>
      Term.Param(List.empty[Mod], fieldName, Some(className), None)
    }
    val replacedTypesClassSource = source"case class $tname(...${List(replacedTypesFields.toList)})"
    replacedTypesClassSource.syntax
  }

  private def ucFirst(str: String): String = Character.toUpperCase(str.charAt(0)) + str.substring(1)

  private case class ClassDefnWithName(name: Type.Name, defn: Defn.Class, originalFieldName: Term.Param.Name)

  private def anyValClassForField(mainClassName: Type.Name, field: Term.Param): ClassDefnWithName = {
    val caseClassName = Type.Name(mainClassName + ucFirst(field.name.syntax))
    val typeName = field.decltpe.getOrElse(Type.Name("String"))
    val anyValClass = q"case class $caseClassName(value: $typeName) extends AnyVal"
    ClassDefnWithName(caseClassName, anyValClass, field.name)
  }
}

