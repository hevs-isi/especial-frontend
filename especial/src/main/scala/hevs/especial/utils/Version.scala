package hevs.especial.utils

/**
 * Project name and version.
 */
object Version {

  private val name = "ESPecIaL"

  val major = "B2"
  val minor = 0

  override def toString = s"$name version $getVersion"

  def getVersion = s"$major.$minor"
}