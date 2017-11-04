package pl.bka.example.anyval

import java.time.Instant
import java.util.UUID

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
