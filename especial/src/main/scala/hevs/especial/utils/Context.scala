package hevs.especial.utils

/**
 * The context of the current execution. Use by all blocks in the pipeline.
 * @param progName the name of the executed program
 * @param loggerEnable enable the QEMU logger to trace events
 */
class Context(val progName: String, loggerEnable: Boolean = false) {

  /**
   * Check if the QEMU logger enabled or not.
   * Used to trace output information from QEMU to the monitor server. Somme additional code is added in the
   * generated code if it is enabled.
   */
  def isQemuLoggerEnabled = loggerEnable

  /** Logger used by the pipeline blocks to report any error or information. */
  val log = new Logger

  override def toString = s"Context of '$progName'."
}
