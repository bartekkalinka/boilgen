package pl.bka.utils

object TextUtils {
  def ucFirst(str: String): String = Character.toUpperCase(str.charAt(0)) + str.substring(1)

  def lcFirst(str: String): String = Character.toLowerCase(str.charAt(0)) + str.substring(1)

  def camelToUnderscores(name: String): String = "[A-Z\\d][a-z]".r.replaceAllIn(name, {m =>
    "_" + m.group(0)
  }).toLowerCase()

}

