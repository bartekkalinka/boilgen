package pl.bka

import java.io.File

import pl.bka.generators.AnyValGen.AnyValCaseClassesOutput
import pl.bka.generators.{AnyValGen, SlickTableGen}
import pl.bka.generators.SlickTableGen.SlickTableOutput
import pl.bka.input.{InputOptions, OutputType}
import pl.bka.matchers.ClassMatcher

import scala.io.Source

object Main {
  def main(args: Array[String]) {
    val inputOptions = InputOptions.parse(args).map(opts => (getInput(opts), opts.outputType))
    val parsedInput = inputOptions.toRight("wrong input options").flatMap {
      case (input, outputType) => ClassMatcher.parseClassWithPackageAndImports(input).map((_, outputType))
    }
    parsedInput match {
      case Right((matcher, outputType)) =>
        val AnyValCaseClassesOutput(anyValClasses, replacedTypesClass, mainClassName, fields) = AnyValGen.generate(matcher)
        outputType match {
          case OutputType.AnyVal =>
            println(anyValClasses)
            println(replacedTypesClass)
          case OutputType.Slick =>
            val SlickTableOutput(slickTable) = SlickTableGen.generate(mainClassName, fields)
            println(slickTable)
        }
      case Left(error) => println(s"error: $error")
    }
  }

  private def getInput(opts: InputOptions): String = {
    val inputFile = if (opts.inputPath == "") {
      Source.fromFile(new File("./src/main/scala/pl/bka/example/input/BankTransaction.scala"))
    } else {
      Source.fromFile(new File(opts.inputPath))
    }
    inputFile.getLines().toSeq.reduce(_ + "\n" + _)
  }
}
