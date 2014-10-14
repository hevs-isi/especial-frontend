package hevs.androiduino.dsl.utils

import java.io.IOException

import grizzled.slf4j.{Logger => Log}

/**
 * Logger class used to print out messages, warnings and errors to the console.
 *
 * This is basically a wrapper of the existing `Logging` class. The method `terminateIfErrors` can be used to
 * automatically terminate the program if any error is exist.
 */
class Logger {

  private val logger = Log(classOf[Logger])

  var hasErrors = false

  def trace(msg: => Any): Unit = logger.trace(msg)

  def debug(msg: => Any): Unit = logger.debug(msg)

  def info(msg: => Any): Unit = logger.info(msg)

  def warn(msg: => Any): Unit = logger.warn(msg)

  def fatal(msg: Any): Nothing = {
    error("Fatal: " + msg)
    sys.exit(1) // return Nothing and terminate here
  }

  def terminateIfErrors() = {
    if (hasErrors) {
      error("Program stopped because of errors !")
      throw new IOException
    }
  }

  def error(msg: => Any): Unit = {
    hasErrors = true // Report an error
    logger.error(msg)
  }
}