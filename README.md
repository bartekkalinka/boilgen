# boilgen
Generating boilerplate with scalameta.

For example input case class:

    package example
    import java.time.Instant
    import java.util.UUID

    case class BankTransaction(
      id: UUID,
      value: Long,
      createDate: Instant,
      modifiedDate: Instant,
      cardNumber: String,
      description: String
    )
    
running `sbt "run -o anyval"` generates case classes extending AnyVal for each field of the input class.  It also outputs original case class with replaced fields types:

    case class BankTransactionId(value: UUID) extends AnyVal
    case class BankTransactionValue(value: Long) extends AnyVal
    case class BankTransactionCreateDate(value: Instant) extends AnyVal
    case class BankTransactionModifiedDate(value: Instant) extends AnyVal
    case class BankTransactionCardNumber(value: String) extends AnyVal
    case class BankTransactionDescription(value: String) extends AnyVal
    case class BankTransaction(
      id: BankTransactionId,
      value: BankTransactionValue,
      createDate: BankTransactionCreateDate,
      modifiedDate: BankTransactionModifiedDate,
      cardNumber: BankTransactionCardNumber,
      description: BankTransactionDescription
    )

running `sbt "run -o slick"` generates (approximate) slick table code for this case class

    case class BankTransactions(tag: BaseTable.Tag) extends BaseTable[BankTransaction](tag, "bank_transactions") {
      import profile.api._
      def id: Rep[BankTransactionId] = column[BankTransactionId]("id")
      def value: Rep[BankTransactionValue] = column[BankTransactionValue]("value")
      def createDate: Rep[BankTransactionCreateDate] = column[BankTransactionCreateDate]("create_date")
      def modifiedDate: Rep[BankTransactionModifiedDate] = column[BankTransactionModifiedDate]("modified_date")
      def cardNumber: Rep[BankTransactionCardNumber] = column[BankTransactionCardNumber]("card_number")
      def description: Rep[BankTransactionDescription] = column[BankTransactionDescription]("description")
      override def * : ProvenShape[BankTransaction] = (id, value, createDate, modifiedDate, cardNumber, description) <> (BankTransaction.apply _.tupled, BankTransaction.unapply)
    }

# Usage

    sbt "run [options]"

      -p, --path <path>
      -o, --output <output type: anyval or slick>

If no path parameter is given, hardcoded example case class is used.  If no output parameter is given, default output option is `anyval`.
