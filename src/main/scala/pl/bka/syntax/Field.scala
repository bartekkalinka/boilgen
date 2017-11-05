package pl.bka.syntax

import scala.meta.Term

case class Field(name: String, tpe: String)

object Field {
  def apply(termParam: Term.Param): Field = Field(termParam.name.syntax, termParam.decltpe.map(_.syntax).getOrElse("String"))
}

