package pl.bka

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
    case Right(output) => println(output)
    case Left(error) => println(s"error $error")
  }
}
