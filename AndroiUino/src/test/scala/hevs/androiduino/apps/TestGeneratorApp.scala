package hevs.androiduino.apps

import hevs.androiduino.dsl.utils.Version

/**
 * Trait for all test cases.
 */
trait TestGeneratorApp extends App {

  val defaultFileName = getClass.getSimpleName.toLowerCase.dropRight(1)
  val ver = Version.getVersion

  def fileName: String = defaultFileName
}