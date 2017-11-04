package pl.bka.example.input

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

