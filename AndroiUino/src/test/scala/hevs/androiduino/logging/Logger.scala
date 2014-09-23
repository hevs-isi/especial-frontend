package hevs.androiduino.logging

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.utils.OSUtils

object Logger extends App with Logging {
  println("Simple print.")	// Without logger
  
  // Not displayed using the default logger configuration
  debug("Debug information.")
  trace("Trace information.")
  
  // Output to the console
  info("Some information.")
  warn("Warning.")
  error("Something terrible.")
  
  println()
  info(s"Running on '${OSUtils.getOsName}'.")
  info(s"Is Linux: ${OSUtils.isLinux}.")
  info(s"Is Windows: ${OSUtils.isWindows}.")
  info(s"Is Other: ${OSUtils.isOther}.")
}