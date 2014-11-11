package hevs.especial.simulation

import java.io._
import java.net.Socket

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.Pin
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonParser._

import scala.collection.mutable
import scala.util.control.Breaks._

class MonitorServerThread(ms: MonitorServer, s: Socket) extends Thread("MonitorServerThread") with Logging {
  // All outputs pins
  private val ioSet = mutable.Set.empty[Pin]
  // All outputs values
  private val ioStates = mutable.ListBuffer.empty[(Pin, Int)]
  private var connected = true
  private var msgCount = 0

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
   * Count the number of received messages (valid or not).
   * @return the number of received message
   */
  def getMessagesSize = msgCount

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
          decodeJson(l)
          msgCount += 1
        }
      }

      // Connection closed by the user
      info("Connection closed.")
      out.close()
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

  // Decode a command received from QEMU
  private def decodeJson(jsonStr: String): Unit = {
    implicit val formats = DefaultFormats // Brings in default date formats etc.

    // Parse the Json command received from QEMU
    val json = parse(jsonStr)
    val cmd = json.extract[Command]

    cmd.id match {
      case DigitalOut => logOutputValue(cmd)
      case PeriphId(id) => sys.error("Unknown command id " + id)
    }
  }

  // Save the value of an output
  private def logOutputValue(cmd: Command): Unit = {
    ioSet add cmd.pin
    ioStates += ((cmd.pin, cmd.value))
    info("New output value for pin " + cmd.pin)
  }
}