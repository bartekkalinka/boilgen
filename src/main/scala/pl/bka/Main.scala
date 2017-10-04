package pl.bka

import pl.bka.FieldsToAnyValCaseClasses.AnyValCaseClassesOutput
import pl.bka.FieldsToSlickTable.SlickTableOutput

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

  FieldsToAnyValCaseClasses.generate(input) match {
    case Right(AnyValCaseClassesOutput(anyValClasses, replacedTypesClass, fields)) =>
      //println(anyValClasses)
      //println(replacedTypesClass)
      FieldsToSlickTable.generate(input,fields) match {
        case Right(SlickTableOutput(slickTable)) =>
          println(slickTable)
        case Left(error) => println(s"error $error")
      }
    case Left(error) => println(s"error $error")
  }
}
