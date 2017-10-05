package pl.bka

import pl.bka.AnyValGen.AnyValCaseClassesOutput
import pl.bka.SlickTableGen.SlickTableOutput

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

  AnyValGen.generate(input) match {
    case Right(AnyValCaseClassesOutput(anyValClasses, replacedTypesClass, mainClassName, fields)) =>
      //println(anyValClasses)
      //println(replacedTypesClass)
      val SlickTableOutput(slickTable) = SlickTableGen.generate(mainClassName, fields)
      println(slickTable)
    case Left(error) => println(s"error $error")
  }
}
