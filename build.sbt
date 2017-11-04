name := "boilgen"

version := "0.1"

scalaVersion := "2.12.3"

libraryDependencies ++= {
  val scalametaV = "1.8.0"
  val scoptV = "3.7.0"
  val enumeratumV = "1.5.12"
  val slickV = "3.2.0"
  val slickPgV = "0.15.0-RC"
  val kebsV = "1.4.1"

  Seq(
    "org.scalameta" %% "scalameta" % scalametaV,
    "com.github.scopt" %% "scopt" % scoptV,
    "com.beachape" %% "enumeratum" % enumeratumV,
    "com.typesafe.slick"  %% "slick" % slickV,
    "com.github.tminglei" %% "slick-pg" % slickPgV,
    "pl.iterators" %% "kebs-slick" % kebsV
  )
}
