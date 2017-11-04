package pl.bka.example.slick

import com.github.tminglei.slickpg._
import pl.iterators.kebs.Kebs

trait PostgresProfile extends ExPostgresProfile with PgDate2Support {
  override val api = PostgresApi

  object PostgresApi
    extends API
      with DateTimeImplicits
      with Kebs
}

object PostgresProfile extends PostgresProfile
