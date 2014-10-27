package hevs.especial.utils

import org.scalatest.FunSuite

class LoggerTest extends FunSuite {
  test("print various messages") {

    val l = new Logger

    // Not displayed using the default logger configuration
    l.debug("Debug information.")
    l.trace("Trace information.")

    // Output to the console
    l.info("An information.")
    l.warn("A warning.")
    assert(!l.hasErrors)

    l.error("A terrible error.")
    assert(l.hasErrors)

    l.warn("Warning")
    l.error("Error")

    intercept[LoggerError] {
      l.terminateIfErrors()
    }

    assert(l.hasErrors)
    l.info("Error detected.")
  }
}