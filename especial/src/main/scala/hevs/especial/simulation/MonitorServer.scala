package hevs.especial.simulation

import java.io.IOException
import java.net.ServerSocket

import grizzled.slf4j.Logging
import hevs.especial.utils.Settings._

/**
 * TCP Monitor Server used to communicate with a QEMU client. Limited to one connection only.
 * @param port the port of the TCP Server
 */
class MonitorServer(port: Int = MONITOR_TCP_CMD_PORT) extends Logging {

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
      System.exit(-1) // Fatal error
    }

    if (server != null) {
      info("Already connected with a client.")
      server
    }
    else {
      info("Waiting for a client...")
      server = new MonitorServerThread(this, listener.accept())
      server.start()
      server
    }
  }

  /**
   * Close the server Thread and the Socket. Do nothing if not connected.
   */
  def close() = {
    if (server != null)
      server.disconnect()

    if (listener != null)
      listener.close()
  }
}
