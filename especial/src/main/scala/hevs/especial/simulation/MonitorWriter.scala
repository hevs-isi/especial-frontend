package hevs.especial.simulation

import java.io.{BufferedReader, OutputStream}
import java.net.Socket

import scala.collection.mutable

class MonitorWriter(s: Socket) extends MonitorThread(s) {

  private val eventsAck = mutable.Queue.empty[Event]

  def ackEvent(evt: Event) = {
    // Signal a new ack to send
    eventsAck += evt // Append to the queue
  }

  /** TCP server Thread */
  override protected def loop(in: BufferedReader, out: OutputStream): Unit = {

    connected = true // Ready

    while (connected) {
      // Send a message to ack the event
      if (eventsAck.nonEmpty) {
        val e = eventsAck.dequeue()
        out.write("ACK0".getBytes) // 4 Bytes, big endian
        out.flush()
        info(s"> ACK sent for event " + e)
      }

      Thread.sleep(100)
    }
  }
}