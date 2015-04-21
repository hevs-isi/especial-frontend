package hevs.especial.utils

/**
 * Project name and version.
 * Name and version added in the header of all generated files.
 *
 * @author Christopher Metrailler (mei@hevs.ch)
 */
object Version {

  private val name = "ESPecIaL"

  val major = "B3"
  val minor = 0

  override def toString = s"$name version $getVersion"

  def getVersion = s"$major.$minor"
}