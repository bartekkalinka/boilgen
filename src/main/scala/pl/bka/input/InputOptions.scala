package pl.bka.input

case class InputOptions(inputPath: String = "", outputType: OutputType = OutputType.AnyVal)

object InputOptions {
  import OutputType._

  val parser = new scopt.OptionParser[InputOptions]("boilgen") {
    head("boilgen")

    opt[String]('p', "path").valueName("<path>").action((x, c) => c.copy(inputPath = x))
    opt[OutputType]('o', "output").valueName(s"<output type: ${OutputType.cmdlineList}>").action((x, c) => c.copy(outputType = x))
  }

  def parse(args: Array[String]): Option[InputOptions] = parser.parse(args, InputOptions())
}

