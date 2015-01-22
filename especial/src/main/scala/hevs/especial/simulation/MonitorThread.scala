package hevs.especial.simulation

import java.io.{InputStreamReader, BufferedReader, OutputStream}
import java.net.{SocketTimeoutException, Socket}

import grizzled.slf4j.Logging

/**
 * Base class used to create TCP socket Thread for the monitor.
 *
 * The Thread implementation is hidden. There are only `start` and `stop` methods. The `loop` function is called
 * automatically. It must be an infinite loop. `connected` must be set to `true` when the Thread is ready and
 * initialized.
 *
 * @param s the socket to connect with
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
abstract class MonitorThread(s: Socket) extends Logging {

  /*
   * Generic Thread loop.
   * Prepare in and out streams and call the loop function.
   */
  private val r = new Runnable {
    override def run(): Unit = {
      info("Client connected.")
      s.setSoTimeout(5000) // Thrown an exception after 5s if no read
      try {

        val out = s.getOutputStream
        val in = new BufferedReader(new InputStreamReader(s.getInputStream))

        // Call the Thread loop method. Must be an infinite loop
        loop(in, out)

        // Connection closed by the user
        info("Connection closed.")
        in.close()
        out.close()
        s.close()
      }
      catch {
        case e: SocketTimeoutException =>
          // Read timeout
          error("> MonitorServer closed. Timeout.")
        case e: Exception =>
          // Client disconnected or socket closed
          error(s"> MonitorServer closed.")
      }
    }
  }

  /**
   * Indicate when a client is connected and when the Thread is ready.
   * Must be set to `true` by each Thread when ready (and initialized).
   */
  protected var connected = false

  /**
   * Start the Thread.
   */
  def start() = {
    new Thread(r).start()
  }

  /**
   * Force to close the server.
   */
  def stop() = connected = false

  /**
   * @see isReady
   */
  def isNotReady = !isReady

  /**
   * Thread is connected and ready.
   * @return `true` if ready, false otherwise.
   */
  def isReady = connected

  /**
   * User thread loop. Must be an infinite loop.
   * @param in the socket input stream
   * @param out the socket output stream
   */
  protected def loop(in: BufferedReader, out: OutputStream): Unit
}
