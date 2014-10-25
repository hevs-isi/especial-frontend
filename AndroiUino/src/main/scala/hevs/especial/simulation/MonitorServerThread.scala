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

  // TCP server Thread
  override def run(): Unit = {
    info("> New client connected.")
    try {
      val out = new PrintWriter(s.getOutputStream, true)
      val in = new BufferedReader(new InputStreamReader(s.getInputStream))
      connected = true
      breakable {
        while (connected) {
          val l = in.readLine()
          if (l == null) {
            break()
          }
          println("Read: " + l)
          messages += l
          Thread.sleep(100)
        }
      }
      connected = false
      info("> Connection closed.")
      out.close()
      in.close()
      s.close()
    }
    catch {
      case e: Exception =>
        connected = false
        info(s"MonitorServer closed.")
        e.printStackTrace()
    }
  }
}