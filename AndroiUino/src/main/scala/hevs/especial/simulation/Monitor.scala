package hevs.especial.simulation

import java.io.IOException
import java.net.ServerSocket

import grizzled.slf4j.Logging

object Monitor extends App {

  final val TCP_CMD_PORT = 14001

  val ms = new MonitorServer(TCP_CMD_PORT)
  val m = ms.waitForClient()

  while (m.isConnected) {
    Thread.sleep(100)
  }
  println("Connection closed !")

  ms.close()
}

class MonitorServer(port: Int) extends Logging {

  var listener: ServerSocket = null
  var server: MonitorServerThread = null

  try {
    listener = new ServerSocket(port)
    info(s"MonitorServer listening on port $port.")
  }
  catch {
    case e: IOException =>
      error(s"Could not listen on port $port.")
      System.exit(-1)
  }

  // Block until a client is connected
  def waitForClient(): MonitorServerThread = {
    if (listener == null) {
      error("MonitorServer not listening !")
      System.exit(-1)
    }
    info("Waiting for a client...")
    server = new MonitorServerThread(this, listener.accept())
    server.start()
    server
  }

  def close() = {
    if (server != null)
      server.disconnect()

    if (listener != null)
      listener.close()
  }
}
