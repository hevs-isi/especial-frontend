package hevs.especial.simulation

import java.io._
import java.net.Socket

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.Pin
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonAST.{JObject, JNothing$}
import net.liftweb.json.JsonParser._

import scala.collection.mutable
import scala.util.control.Breaks._

class MonitorReader(s: Socket) extends Thread("MonitorReader") with Logging {

  // All outputs pins
  private val ioPin = mutable.Set.empty[Pin]

  /* Messages */
  // All outputs values
  private val ioStates = mutable.ListBuffer.empty[(Pin, Int)]
  // Store all received events with their value.
  // Queue not used because we want to store all events after execution.
  private val events = mutable.ListBuffer.empty[Event]


  /* Events */
  // `true` when ready and connected with a TCP client
  private var connected = false
  private var lastEventIdx = 0

  /**
   * Check if a client is connected or not.
   * @return true if the client is connected, false otherwise
   */
  def isConnected = connected

  /**
   * Force to close the server.
   */
  def disconnect() = connected = false

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
    group.map {
      case (pin, list) => pin -> list.map(x => x._2)
    }
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
  override def run(): Unit = {
    info("New client connected.")
    try {
      val in = new BufferedReader(new InputStreamReader(s.getInputStream))

      connected = true
      breakable {
        while (connected) {
          val l = in.readLine()
          if (l == null)
            break()

          trace("Read: " + l)
          val valid = decodeJson(l)
          if (!valid)
            break()
        }
      }

      // Connection closed by the user
      info("Connection closed.")
      in.close()
      s.close()
    }

    catch {
      // Client disconnected or socket closed
      case e: Exception =>
        connected = false
        info(s"> MonitorServer closed.")
        e.printStackTrace()
    }
  }

  /**
   * Decode a Json message received from QEMU.
   * @param jsonStr the received string from QEMU
   * @return `true` if the Json message was decoded sucessfully, `false` otherwise
   */
  private def decodeJson(jsonStr: String): Boolean = {
    // Parse the Json command received from QEMU
    val json = parse(jsonStr)

    // Check if the pin number is available or not.
    // Extract the message or the command from the Json message.
    implicit val formats = DefaultFormats // Brings in default date formats etc.
    val read = json \ "pin" match {
        case _: JObject => json.extract[Command].asInstanceOf[Command]
        case _ => json.extract[Event].asInstanceOf[Event]
      }

    read.id match {
      case DigitalOut | CEvent =>
        logMessageOrEvent(read)
        true
      case MsgId(id) =>
        error("Unknown message id " + id)
        false // Invalid JSON message
    }
  }

  // Save the command or the event
  private def logMessageOrEvent(msg: JsonMessage) = msg match {
    case cmd: Command =>
      ioPin add cmd.pin
      ioStates += ((cmd.pin, cmd.value))
    case evt: Event =>
      events += evt // Add the new event to the end of the linked list.
  }
}