package hevs.especial.utils

/**
 * Contains the context of the current execution. Use by all blocks of the pipeline.
 */
class Context(val progName: String, loggerEnable: Boolean = false) {

  /** Qemu logger to trace output information from QEMU. */
  private var isLoggerEnabled = loggerEnable

  def enableQemuLogger() = isLoggerEnabled = true
  def isQemuLoggerEnabled = isLoggerEnabled


  /** Logger used by the pipeline blocks to report any error or information. */
  val log = new Logger
}
