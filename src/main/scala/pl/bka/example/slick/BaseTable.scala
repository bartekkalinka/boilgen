package pl.bka.example.slick

abstract class BaseTable[T](tag: BaseTable.Tag, tableName: String) extends BaseTable.profile.Table[T](tag, tableName) {
  protected val profile: PostgresProfile = BaseTable.profile
}

object BaseTable {
  protected val profile = PostgresProfile
  type Tag = profile.api.Tag
}

