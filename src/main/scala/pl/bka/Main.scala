package pl.bka

import pl.bka.generators.AnyValGen.AnyValCaseClassesOutput
import pl.bka.generators.{AnyValGen, SlickTableGen}
import pl.bka.generators.SlickTableGen.SlickTableOutput
import pl.bka.matchers.ClassMatcher

object Main extends App {
  val input =
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

  ClassMatcher.parse(input) match {
    case Right(matcher) =>
      val AnyValCaseClassesOutput(anyValClasses, replacedTypesClass, mainClassName, fields) = AnyValGen.generate(matcher)
      //println(anyValClasses)
      //println(replacedTypesClass)
      val SlickTableOutput(slickTable) = SlickTableGen.generate(mainClassName, fields)
      println(slickTable)
    case Left(error) => println(s"error $error")
  }
}
