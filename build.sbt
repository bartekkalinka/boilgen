name := "boilgen"

version := "0.1"

scalaVersion := "2.12.3"

libraryDependencies ++= {
  val scalametaV = "1.8.0"
  val scoptV = "3.7.0"
  val enumeratumV = "1.5.12"

  Seq(
    "org.scalameta" %% "scalameta" % scalametaV,
    "com.github.scopt" %% "scopt" % scoptV,
    "com.beachape" %% "enumeratum" % enumeratumV
  )
}
