package hevs.especial.simulation

import java.io.IOException
import java.net.{ServerSocket, SocketTimeoutException}

import grizzled.slf4j.Logging
import hevs.especial.utils.Settings

/**
 * Monitor used to communicate with a QEMU client over TCP.
 */
class Monitor extends Logging {

  var reader: MonitorReader = null
  var writer: MonitorWriter = null

  private var cmdListener: ServerSocket = null
  private var evtListener: ServerSocket = null

  startServers()

  /**
   * Blocking function util a client is connected.
   * @return `true` if a client is connected, `false` if timeout
   */
  def waitForClient(): Boolean = {
    if (cmdListener == null) {
      error("MonitorServer not listening !")
      System.exit(-1) // Fatal error
    }

    if (reader != null && writer != null) {
      info("Already connected.")
      return true
    }

    info("Waiting for a client...")

    // Blocking until a client is connected
    cmdListener.setSoTimeout(8000)
    evtListener.setSoTimeout(8000)

    try {
      reader = new MonitorReader(cmdListener.accept())
      reader.start()


      writer = new MonitorWriter(evtListener.accept())
      writer.start()
    }
    catch {
      case e: SocketTimeoutException =>
        error("Connection timeout.")
        return false
    }

    // Wait until the initialization is completed
    while (reader.isNotReady || writer.isNotReady)
      Thread.sleep(100)
    true
  }

  /**
   * Close the server Thread and the Socket. Do nothing if not connected.
   */
  def close() = {
    if (reader != null)
      reader.stop()

    if (cmdListener != null)
      cmdListener.close()

    if (writer != null)
      writer.stop()

    if (evtListener != null)
      evtListener.close()
  }

  private def startServers() {
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
}
