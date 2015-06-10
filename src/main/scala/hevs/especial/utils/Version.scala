package hevs.especial.utils

/**
 * Project name and version.
 * Name and version added in the header of all generated files.
 *
 * @author Christopher Metrailler (mei@hevs.ch)
 */
object Version {

  private val name = "ESPecIaL"

  val major = "0"
  val minor = 4

  override def toString = s"$name version $getVersion"

  def getVersion = s"$major.$minor"
}