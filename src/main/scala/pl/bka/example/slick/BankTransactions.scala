package pl.bka.example.slick

import pl.bka.example.anyval._
import slick.lifted.ProvenShape

case class BankTransactions(tag: BaseTable.Tag) extends BaseTable[BankTransaction](tag, "bank_transactions") {
  import profile.api._
  def id: Rep[BankTransactionId] = column[BankTransactionId]("id")
  def value: Rep[BankTransactionValue] = column[BankTransactionValue]("value")
  def createDate: Rep[BankTransactionCreateDate] = column[BankTransactionCreateDate]("create_date")
  def modifiedDate: Rep[BankTransactionModifiedDate] = column[BankTransactionModifiedDate]("modified_date")
  def cardNumber: Rep[BankTransactionCardNumber] = column[BankTransactionCardNumber]("card_number")
  def description: Rep[BankTransactionDescription] = column[BankTransactionDescription]("description")
  override def * : ProvenShape[BankTransaction] = (id, value, createDate, modifiedDate, cardNumber, description) <>
    ((BankTransaction.apply _).tupled, BankTransaction.unapply)
}


