package hevs.especial.simulation

import java.io._
import java.net.Socket

import grizzled.slf4j.Logging

import scala.collection.mutable
import scala.util.control.Breaks._

class MonitorServerThread(ms: MonitorServer, s: Socket) extends Thread("MonitorServerThread") with Logging {

  private val messages = mutable.ListBuffer.empty[String]
  private var connected = true

  // Client connected or not
  def isConnected = connected

  // Force to close the server
  def disconnect() = connected = false

  def getMessagesSize = messages.size

  // TCP server Thread
  override def run(): Unit = {
    info("New client connected.")
    try {
      val out = new PrintWriter(s.getOutputStream, true)
      val in = new BufferedReader(new InputStreamReader(s.getInputStream))

      out.write("1234\n".toCharArray)
      out.flush()

      connected = true
      breakable {
        while (connected) {
          val l = in.readLine()
          if (l == null)
            break()
          info("Read: " + l)
          messages += l

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

}