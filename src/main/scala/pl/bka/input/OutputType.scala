package pl.bka.input

import enumeratum.{Enum, EnumEntry}

sealed trait OutputType extends EnumEntry
object OutputType extends Enum[OutputType] {

  case object AnyVal extends OutputType
  case object Slick extends OutputType

  val values = findValues

  val cmdlineList = values.map(_.entryName.toLowerCase).reduce(_ + " or " + _)

  implicit val weekDaysRead: scopt.Read[OutputType] =
    scopt.Read.reads(OutputType.withNameInsensitive)
}

