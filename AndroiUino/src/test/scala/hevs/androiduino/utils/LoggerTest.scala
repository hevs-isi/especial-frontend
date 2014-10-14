package hevs.androiduino.utils

import java.io.IOException

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.utils.{Logger, OSUtils}
import org.scalatest.FunSuite

class LoggerTest extends FunSuite with Logging {
  test("print error and terminates") {
    println("Simple print.") // Without logger

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

    val l = new Logger
    l.warn("Warning")
    assert(!l.hasErrors)
    l.error("Error")
    assert(l.hasErrors)

    // sys.exit() thrown a SecurityException
    intercept[IOException] {
      l.terminateIfErrors()
    }

    l.info("Never executed :(")
  }
}