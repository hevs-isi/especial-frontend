package hevs.especial.simulation

import java.io._
import java.math.BigInteger
import java.net.Socket

import grizzled.slf4j.Logging

import scala.util.control.Breaks._

// TODO: use a base class. Implement run basic and add loop() abstract...

class MonitorWriter(s: Socket) extends Thread("MonitorWriter") with Logging {

  // `true` when ready and connected with a TCP client
  private var connected = false
  private var newEventAck = false

  /**
   * Check if a client is connected or not.
   * @return true if the client is connected, false otherwise
   */
  def isConnected = connected

  /**
   * Force to close the server.
   */
  def disconnect() = connected = false


  def ackEvent() = {
    assert(!newEventAck) // FIXME: use a queue
    // Signal a new ack to send
    newEventAck = true
  }

  /** TCP server Thread */
  override def run(): Unit = {
    info("New client connected.")
    try {
      val out = s.getOutputStream

      connected = true
      while (connected) {

        // Send a message to ack the event
        if (newEventAck) {
          out.write("ACK0".getBytes)
          out.flush()
          info("> ACK0 sent")
          newEventAck = false
        }

        Thread.sleep(100)
      }

      // Connection closed by the user
      info("Connection closed.")
      out.close()
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
}