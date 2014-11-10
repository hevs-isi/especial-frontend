package hevs.especial.utils

/**
 * Project name and version.
 */
object Version {

  private val name = "ESPecIaL"

  val major = "a1"
  val minor = 1

  override def toString = s"$name version $getVersion"

  def getVersion = s"$major.$minor"
}