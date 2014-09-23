package hevs.androiduino.apps

import hevs.androiduino.dsl.utils.Version

/**
 * Trait for all test cases
 */
trait TestGeneratorApp extends App {
  // Convert class name to string
  val appName = this.getClass().getName().split('.').last.takeWhile(_ != '$').toLowerCase()
  val ver = Version.getVersion

  println("*".*(60))
  println(s"AndroidUINO generator / mui 2013")
  println(s"Version $ver - Example $appName")
  println("*".*(80))
}