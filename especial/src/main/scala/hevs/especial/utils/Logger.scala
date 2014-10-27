package hevs.especial.utils

import grizzled.slf4j.{Logger => Log}

/**
 * Logger class used to print out messages, warnings and errors to the console.
 *
 * This is basically a wrapper of the existing `Logging` class. The method `terminateIfErrors` can be used to
 * automatically terminate the program if any error has occurred.
 */
class Logger {

  private val logger = Log(classOf[Logger])

  /**
   * Errors have occurred or not.
   */
  var hasErrors = false

  /**
   * Warnings have occurred or not.
   */
  var hasWarnings = false

  def reset() = {
    hasErrors = false
    hasWarnings = false
  }

  /**
   * Trace a message.
   * @param msg message to print
   */
  def trace(msg: => Any): Unit = logger.trace(msg)

  /**
   * Print a debug a message.
   * @param msg message to print
   */
  def debug(msg: => Any): Unit = logger.debug(msg)

  /**
   * Print an information a message.
   * @param msg message to print
   */
  def info(msg: => Any): Unit = logger.info(msg)

  /**
   * Print a warning message.
   * @param msg message to print
   */
  def warn(msg: => Any): Unit = {
    hasWarnings = true // Report a warning
    logger.warn(msg)
  }

  /**
   * Print an error.
   * @param msg message to print
   */
  def error(msg: => Any): Unit = {
    hasErrors = true // Report an error
    logger.error(msg)
  }

  /**
   * Fatal error. Print an error and terminates directly.
   * @param msg error message to print
   */
  def fatal(msg: Any): Nothing = {
    error("Fatal: " + msg)
    sys.exit(1) // return Nothing and terminate here
  }

  /**
   * Errors have occurred. Throw a `LoggerError` exception to terminate the program and clean previous errors.
   */
  def terminateIfErrors() = {
    if (hasErrors)
      throw new LoggerError("Program stopped because of errors !")
  }
}