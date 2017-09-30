package pl.bka

import pl.bka.FieldsToAnyValCaseClasses.AnyValCaseClassesOutput

object Main extends App {
  val input =
    """
      |case class Transaction(
      |  id: UUID,
      |  value: Long,
      |  createDate: Instant,
      |  modifiedDate: Instant,
      |  cardNumber: String,
      |  description: String
      |)
    """.stripMargin

  FieldsToAnyValCaseClasses.generate(input) match {
    case Right(AnyValCaseClassesOutput(anyValClasses, replacedTypesClass)) =>
      println(anyValClasses)
      println(replacedTypesClass)
    case Left(error) => println(s"error $error")
  }
}
