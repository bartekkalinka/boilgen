package pl.bka

import java.io.File

import pl.bka.generators.AnyValGen.AnyValCaseClassesOutput
import pl.bka.generators.{AnyValGen, SlickTableGen}
import pl.bka.generators.SlickTableGen.SlickTableOutput
import pl.bka.input.InputOptions
import pl.bka.matchers.ClassMatcher

import scala.io.Source

object Main {
  def main(args: Array[String]) {
    val inputOption = argsToInput(args)
    val parsedInput = inputOption.toRight("wrong input options").flatMap(ClassMatcher.parse)
    parsedInput match {
      case Right(matcher) =>
        val AnyValCaseClassesOutput(anyValClasses, replacedTypesClass, mainClassName, fields) = AnyValGen.generate(matcher)
        //println(anyValClasses)
        //println(replacedTypesClass)
        val SlickTableOutput(slickTable) = SlickTableGen.generate(mainClassName, fields)
        println(slickTable)
      case Left(error) => println(s"error: $error")
    }
  }

  private def argsToInput(args: Array[String]): Option[String] =
    InputOptions.parse(args).map {
      opts => if(opts.inputPath == "") {
        defaultInput
      } else {
        Source.fromFile(new File(opts.inputPath)).getLines().toSeq.reduce(_ + "\n" + _)
      }
    }

  private val defaultInput =
    """
      |case class BankTransaction(
      |  id: UUID,
      |  value: Long,
      |  createDate: Instant,
      |  modifiedDate: Instant,
      |  cardNumber: String,
      |  description: String
      |)
    """.stripMargin
}
