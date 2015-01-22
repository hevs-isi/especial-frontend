package hevs.especial.simulation

import java.io.{BufferedReader, OutputStream}
import java.net.Socket

import scala.collection.mutable

/**
 * TCP Thread used to communicate with QEMU over TCP/IP.
 * Raw messages (4 bytes) are sent to QEMU to acknowledge an event or to set the input value of a button.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class MonitorWriter(s: Socket) extends MonitorThread(s) {

  /** Events send to QEMU to control the code execution. */
  private val events = mutable.Queue.empty[Event]

  /**
   * Send back a confirmation to QEMu when an event has been received.
   *
   * @param evt the event to confirm
   */
  def ackEvent(evt: Event): Unit = {
    // Signal a new ack to send
    events += evt // Append to the queue
  }

  /**
   * Update a digital input value when the code is running in QEMU.
   *
   * For now, input value can only be set for the 4 digital input buttons. The port number is ignored and the pin
   * number must 0,1,2 or 3 for each 4 input buttons.
   *
   * @param btId the button number (0,1,2 or 3)
   * @param value the new input value
   */
  def setButtonInputValue(btId: Int, value: Boolean): Unit = {
    assert(btId < 4)
    val boolValue: Byte = if (value) 1 else 0
    val inVal: Array[Byte] = Array('V'.toByte, 0.toByte, btId.toByte, boolValue)
    events += new ByteEvent(DigitalOut.id, inVal)
  }

  /**
   * TCP server Thread.
   * Send commands and events stored in the [[events]] queue to the QEMU TCP Thread.
   * Send ACk of QEMU logger events or sent input values (button boolean values).
   */
  override protected def loop(in: BufferedReader, out: OutputStream): Unit = {

    connected = true // Ready

    while (connected) {
      if (events.nonEmpty) {
        events.dequeue() match {
          /** Set an input value to QEMU. */
          case e: ByteEvent =>
            out.write(e.byteValue) // 4 bytes with the port, pin and value (big endian)
            out.flush()
            trace(s"> Input value sent: $e")

          /** Ack a C event from the Qemu logger. */
          case e: Event =>
            out.write("ACK0".getBytes) // 4 bytes, big endian
            out.flush()
            trace(s"> ACK sent for event $e")
        }
      }

      Thread.sleep(100)
    }
  }
}