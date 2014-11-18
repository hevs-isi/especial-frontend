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

  // Reader from QEMU
  var cmdListener: ServerSocket = null
  var cmdServer: MonitorReader = null

  // Writer to QEMU
  var evtListener: ServerSocket = null
  var evtServer: MonitorWriter = null

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
  def waitForClient(): (MonitorReader, MonitorWriter) = {
    if (cmdListener == null) {
      error("MonitorServer not listening !")
      System.exit(-1) // Fatal error
    }

    if (cmdServer != null) {
      info("Already connected with a client.")
      (cmdServer, evtServer)
    }
    else {
      info("Waiting for a client...")

      // Blocking until a client is connected
      cmdServer = new MonitorReader(cmdListener.accept())
      cmdServer.start()

      evtServer = new MonitorWriter(evtListener.accept())
      evtServer.start()

      // Wait until the initialization is completed
      while(!cmdServer.isConnected)
        Thread.sleep(100)

      (cmdServer, evtServer)
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
