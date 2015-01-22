package hevs.especial.simulation

import java.io._
import java.net.Socket

import hevs.especial.dsl.components.Pin
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonParser._

import scala.collection.mutable

/**
 * TCP Thread used to read events sent from QEMU.
 *
 * Events are formatted in Json.
 * All output values sent from QEMu are stored, so a VCD file can be generated at the end of the test.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class MonitorReader(s: Socket) extends MonitorThread(s) {

  // All outputs pins
  private val ioPin = mutable.Set.empty[Pin]

  // All outputs values
  private val ioStates = mutable.ListBuffer.empty[(Pin, Int)]

  // Store all received events with their value.
  // Queue not used because we want to store all events after execution.
  private val events = mutable.ListBuffer.empty[Event]
  private var lastEventIdx = 0

  /**
   * Return all values of each outputs.
   * @return values for each outputs
   */
  def getOutputValues: Map[Pin, Seq[Int]] = {
    // Group values by pins (filter and group) and return only the value of the input
    // Map(GPIOC#13 -> ListBuffer((GPIOC#13,1)), GPIOC#12 -> ListBuffer((GPIOC#12,1), (GPIOC#12,0)))
    val group = ioStates.groupBy(_._1)

    // Return only the value of the output
    // Map(GPIOC#13 -> ListBuffer(1), GPIOC#12 -> ListBuffer(1, 0))
    val res = group.map {
      case (pin, list) => pin -> list.drop(2).map(x => x._2) // Remove initialization values
    }
    res.filter(x => x._2.length > 0) // Remove entries with zero values
  }

  /**
   * All defined outputs.
   * @return a set of defined output in qemu
   */
  def getOutputPins: Set[Pin] = ioPin.toSet // Convert to immutable

  /**
   * Wait for an event. Blocking method until the event is received.
   * Search only for new events. Past events are stored but ignored in this case.
   * @param evt the event to wait one
   */
  def waitForEvent(evt: Event): Unit = {
    // Blocking method
    while (true) {
      // Search for the event in the current range
      val r: Range = lastEventIdx until events.size by 1
      for (idx <- r if evt equals events(idx)) {
        // Event found. Update the new range (can be 0 if no new events are received).
        lastEventIdx = idx + 1
        return // Exit when the first event is found in the range
      }

      Thread.sleep(100) // Wait some time so new events can be added in the queue
    }
  }

  /**
   * Search for an event in the current event list.
   * @param evt the event to search for
   * @return `true` if found, `false` otherwise
   */
  def hasEvent(evt: Event): Boolean = events.contains(evt)

  /** TCP server Thread */
  override protected def loop(in: BufferedReader, out: OutputStream): Unit = {
    connected = true // Ready

    while (connected) {
      val l = in.readLine()
      if (l != null)
        decodeJson(l) // Decode and store
    }
  }

  /**
   * Decode a Json message received from QEMU and store the message if valid.
   * @param jsonStr the received string from QEMU
   * @return `true` if the Json message was decoded successfully, `false` otherwise
   */
  private def decodeJson(jsonStr: String): Boolean = {
    // Parse the Json command received from QEMU
    val json = parse(jsonStr)

    // Check if the pin number is available or not.
    // Extract the message or the command from the Json message.
    implicit val formats = DefaultFormats // Brings in default date formats etc.
    val read = json \ "pin" match {
        case _: JObject => json.extract[Command] //.asInstanceOf[Command]
        case _ => json.extract[Event] //.asInstanceOf[Event]
      }

    read.id match {
      case DigitalOut | CEvent =>
        logMessageOrEvent(read)
        true
      case MsgId(id) =>
        error(s"Unknown message id '$id'.")
        false // Invalid JSON message
    }
  }

  /** Save the command or the event. */
  private def logMessageOrEvent(msg: JsonMessage) = msg match {
    case cmd: Command =>
      ioPin add cmd.pin
      ioStates += ((cmd.pin, cmd.value))
    case evt: Event =>
      events += evt // Add the new event to the end of the linked list.
  }
}