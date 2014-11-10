package hevs.especial.simulation

import java.io.IOException
import java.net.ServerSocket

import grizzled.slf4j.Logging
import hevs.especial.utils.Settings
import hevs.especial.utils.Settings._

/**
 * TCP Monitor Server used to communicate with a QEMU client.
 */
class MonitorServer() extends Logging {

  var cmdListener: ServerSocket = null
  var cmdServer: MonitorServerThread = null

  var evtListener: ServerSocket = null
  var evtServer: MonitorServerThread = null

  startServers()

  def startServers() {
    try {
      cmdListener = new ServerSocket(Settings.MONITOR_TCP_CMD_PORT)
      evtListener = new ServerSocket(Settings.MONITOR_TCP_EVT_PORT)
      info(s"MonitorServer started.")
    }
    catch {
      case e: IOException =>
        error(s"Could not listen.")
        System.exit(-1)
    }
  }

  // Block until a client is connected
  def waitForClient(): MonitorServerThread = {
    if (cmdListener == null) {
      error("MonitorServer not listening !")
      System.exit(-1) // Fatal error
    }

    if (cmdServer != null) {
      info("Already connected with a client.")
      cmdServer
    }
    else {
      info("Waiting for a client...")

      cmdServer = new MonitorServerThread(this, cmdListener.accept())
      cmdServer.start()

      evtServer = new MonitorServerThread(this, evtListener.accept())
      evtServer.start()

      cmdServer
    }
  }

  /**
   * Close the server Thread and the Socket. Do nothing if not connected.
   */
  def close() = {
    if (cmdServer != null)
      cmdServer.disconnect()

    if (cmdListener != null)
      cmdListener.close()

    if (evtServer != null)
      evtServer.disconnect()

    if (evtListener != null)
      evtListener.close()
  }
}
