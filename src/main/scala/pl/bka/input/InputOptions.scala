package pl.bka.input

case class InputOptions(inputPath: String = "")

object InputOptions {
  val parser = new scopt.OptionParser[InputOptions]("boilgen") {
    head("boilgen")

    opt[String]('p', "path").valueName("<path>").action((x, c) => c.copy(inputPath = x))
  }

  def parse(args: Array[String]): Option[InputOptions] = parser.parse(args, InputOptions())
}

