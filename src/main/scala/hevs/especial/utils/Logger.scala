/* Copyright 2009-2014 EPFL, Lausanne */
package hevs.especial.utils

import grizzled.slf4j.{Logger => Log}

/**
 * Logger class used to print out messages, warnings and errors to the console.
 *
 * This is basically a wrapper of the existing `Logging` class. The method `terminateIfErrors` can be used to
 * automatically terminate the program if any error has occurred.
 * The logger can be configured using the file `src/main/resources/simplelogger.properties`.
 *
 * Code adapter from: [[https://github.com/epfl-lara/leon/blob/master/src/main/scala/leon/Reporter.scala]]
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Logger {

  private val logger = Log("ContextLogger")

  /**
   * Warnings have occurred or not.
   */
  var hasWarnings = false

  /**
   * Errors have occurred or not.
   */
  var hasErrors = false

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
    logger.warn(msg)
    hasWarnings = true // Report a warning
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
   * Print an error.
   * @param msg message to print
   */
  def error(msg: => Any): Unit = {
    logger.error(msg)
    hasErrors = true // Report an error
  }

  /**
   * @see terminateIfErrors
   */
  def terminateIfErrors(): Unit = terminateIfErrors(null)

  /**
   * Errors have occurred. Throw a `LoggerError` exception to terminate the program and clean previous errors.
   * @param p the pipeline currently executed or null if not used
   */
  def terminateIfErrors(p: Pipeline[_, _]): Unit = {
    if (hasErrors) {
      // Print a verbose error with the name of the last pipeline block executed (if available).
      if (p != null)
        throw new LoggerError(s"Program stopped because of errors in the '${p.currentName}' block !\nPipeline: $p")
      else
        throw new LoggerError(s"Program stopped because of errors !")
    }
  }
}