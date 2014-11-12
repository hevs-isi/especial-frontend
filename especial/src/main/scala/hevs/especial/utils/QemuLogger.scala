package hevs.especial.utils

/**
 * Use to trace event of the code running in QEMU.
 *
 * The logger is called automatically in the generated C code to trace event of the code. These events are received
 * by the Scala monitor to check code results and its behaviour.
 */
object QemuLogger {

  private final val LOGGER_NAME = "QemuLogger"

  final val addStartEvent = printEvent("SECTION_START")
  final val addEndInitEvent = printEvent("SECTION_END_INIT")
  final val addLoopStartEvent = printEvent("SECTION_LOOP_START")
  final val addLoopTickEvent = printEvent("SECTION_LOOP_TICK")
  final val addLoopExitEvent = printEvent("SECTION_LOOP_EXIT")

  private def printEvent(event: String) = {
      s"$LOGGER_NAME::send_event($event);\n" // C code to call the logger
  }
}
