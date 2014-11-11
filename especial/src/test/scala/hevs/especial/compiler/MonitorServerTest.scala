package hevs.especial.compiler

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.Pin
import hevs.especial.simulation.MonitorServer

object MonitorServerTest extends App with Logging {
  // Monitor
  val ms = new MonitorServer()

  info("Start monitor. Wait for client...")
  val m = ms.waitForClient()
  var s = m.getMessagesSize

  while (s < 6) {
    s = m.getMessagesSize
    Thread.sleep(100)
  }

  info("6 commands received")
  val v = m.getOutputValues

  // Close the TCP server
  info("Disconnect server.")
  m.disconnect()
  ms.close()

  // Print output values
  printOutputValues(v)

  // Check values for one specific output
  info("val: " + v(Pin('C', 12)))
  val valid = v(Pin('C', 12)) == Seq(1, 0, 1, 0, 1)
  assert(valid)

  info("End")
  System.exit(0)

  // Format output values and print them
  private def printOutputValues(values: Map[Pin, Seq[Int]]) = {
    values foreach (x => info(s"Pin ${x._1}: ${x._2.mkString("-")}"))
  }

}
