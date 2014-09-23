package hevs.androiduino.dsl.utils

object OSUtils {

  sealed abstract class Os
  case class Windows() extends Os
  case class Linux() extends Os
  case class Other() extends Os

  private var osName: String = null

  def getOsName(): String = {
    if (osName == null)
      osName = System.getProperty("os.name").toLowerCase
    osName
  }

  def getOsType(): Os = {
    if (getOsName().startsWith("windows"))
      Windows()
    else if (getOsName().contains("linux"))
      Linux()
    else
      Other()
  }

  def isWindows = getOsType.isInstanceOf[Windows]

  def isLinux = getOsType.isInstanceOf[Linux]

  def isOther = getOsType.isInstanceOf[Other]
}