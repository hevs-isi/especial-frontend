package hevs.androiduino.apps

import hevs.androiduino.dsl.utils.Version
import org.scalatest.FunSuite

/**
 * Trait for all test cases.
 */
abstract class TestGeneratorApp extends FunSuite {

  val defaultFileName = getClass.getSimpleName.toLowerCase
  val ver = Version.getVersion

  def fileName: String = defaultFileName
}