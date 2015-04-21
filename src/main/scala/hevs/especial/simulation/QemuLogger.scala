package hevs.especial.simulation

import hevs.especial.dsl.components.HwImplemented
import hevs.especial.simulation.Events._
import hevs.especial.utils.Settings

/**
 * Use to trace event of the code running in QEMU.
 *
 * The logger is called automatically in the generated C code to trace event of the code. These events are received
 * by the Scala monitor to check code results and its behaviour.
 * See the `qemulogger.h` file for more information.
 *
 * @version 1.1
 * @author Christopher Metrailler (mei@hevs.ch)
 */
object QemuLogger {

  // Name of events.
  // See the C `EventId_t` structure in file `qemulogger.h` for more information.
  private val EVENTS_NAMES: Map[Event, String] = Map(
    MainStart -> "SECTION_START", EndInit -> "SECTION_INIT_END",
    LoopStart -> "SECTION_LOOP_START", LoopTick -> "SECTION_LOOP_TICK",
    MainEnd -> "SECTION_END")

  val addStartEvent = addEvent(MainStart)
  val addEndInitEvent = addEvent(EndInit)
  val addLoopStartEvent = addEvent(LoopStart)
  val addLoopTickEvent = addAckEvent(LoopTick)    // ACK required on each loop iterations
  val addLoopExitEvent = addEvent(MainEnd)

  /** Create a QEMU event which must be acknowledge from the Scala side. */
  private def addAckEvent(evt: Event): String = addEvent(evt, ack = true)

  /** Create a QEMU event which print a debug information to the Scala side. */
  private def addEvent(evt: Event, ack: Boolean = false) = {
    val name = EVENTS_NAMES(evt)

    // Check if events acknowledge is enabled
    var sAck = ""
    if(Settings.MONITOR_ACK_EVENTS)
      sAck = if(ack) ", true" else "" // Default parameter is false

    s"QemuLogger::send_event($name$sAck);\n" // C code to call the logger
  }
}

/**
 * Add to anny component to ability to use the Qemu logger.
 *
 * An include file will be append to other.
 */
trait QemuLogger extends HwImplemented {

  // Include the necessary to use the logger
  override def getIncludeCode = super.getIncludeCode :+ "utils/qemulogger.h"

  // Logger events are added by the code generator directly to force a specific position on the code.
}