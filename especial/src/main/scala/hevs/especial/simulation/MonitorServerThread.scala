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

class MonitorServerThread(ms: MonitorServer, s: Socket) extends Thread("MonitorServerThread") with Logging {

  private var connected = true

  /**
   * Check if a client is connected or not.
   * @return true if the client is connected, false otherwise
   */
  def isConnected = connected

  /**
   * Force to close the server.
   */
  def disconnect() = connected = false


  /* Messages */
  private var msgCount = 0

  // All outputs pins
  private val ioSet = mutable.Set.empty[Pin]

  // All outputs values
  private val ioStates = mutable.ListBuffer.empty[(Pin, Int)]

    /**
   * Count the number of valid received messages.
   * @return the number of received message
   */
  def getValidMessagesSize = msgCount

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
  def getOutputPins: Set[Pin] = ioSet.toSet // Convert to immutable



  /* Events */

  // Store all received events with their value
  private val events = mutable.ListBuffer.empty[Event]


  def getEvents = events.toSeq // Convert to immutable

  def getEventSize = events.size

  def getLastEvent: Option[Event] = events match {
      case c if c.size == 0 => None
      case _ => Some(events.last)
  }

  def waitForLastEvent(evt: Event): Unit = {
    // Must be defined and match with the current event
    while (!(getLastEvent.isDefined && getLastEvent.get.equals(evt))) {
      Thread.sleep(100) // Wait some time for a new event
    }
  }

  // TODO: add more function to wait for event id, values, etc.


  /** TCP server Thread */
  override def run(): Unit = {
    info("New client connected.")
    try {
      val out = new PrintWriter(s.getOutputStream, true)
      val in = new BufferedReader(new InputStreamReader(s.getInputStream))

      connected = true
      breakable {
        while (connected) {
          val l = in.readLine()
          if (l == null)
            break()
          trace("Read: " + l)
          val valid = decodeJson(l)
          if(!valid)
            break()
        }
      }

      // Connection closed by the user
      info("Connection closed.")
      out.close()
      in.close()
      s.close()
      System.exit(-1)
    }

    catch {
      // Client disconnected or socket closed
      case e: Exception =>
        connected = false
        info(s"> MonitorServer closed.")
        e.printStackTrace()
    }
  }

  // Decode a command received from QEMU
  private def decodeJson(jsonStr: String): Boolean = {
    implicit val formats = DefaultFormats // Brings in default date formats etc.

    // Parse the Json command received from QEMU
    val json = parse(jsonStr)
   // val cmd = json.extract[Command] // Extract to predefined case classes

    val read = json \ "pin" match {
      case _: JObject => json.extract[Command].asInstanceOf[Command]
      case _ => json.extract[Event].asInstanceOf[Event]
    }

    read.id match {
      case DigitalOut | CEvent =>
        info(read)
        logMessageOrEvent(read)
        true

      case MsgId(id) =>
        error("Unknown message id " + id)
        false // Invalid JSON message
    }
  }

  // Save the value of an output or an event value
  private def logMessageOrEvent(msg: JsonMessage) = msg match {
    case cmd: Command =>
      ioSet add cmd.pin
      ioStates += ((cmd.pin, cmd.value))
      msgCount += 1
    case evt: Event => events += evt
  }
}