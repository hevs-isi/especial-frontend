package hevs.especial.compiler

import grizzled.slf4j.Logging
import hevs.especial.compiler.CompilerPathTest._
import hevs.especial.simulation.MonitorServer

object MonitorServerTest extends App with Logging {
  // Monitor
  val ms = new MonitorServer()

  info("Start monitor. Wait for client...")
  val m = ms.waitForClient()
  var s = m.getMessagesSize

  while (s < 10) {
    s = m.getMessagesSize
    Thread.sleep(100)
  }

  // Close the TCP server
  info("Disconnect server.")
  m.disconnect()
  ms.close()

  // Close QEMU
  info("Kill QEMU.")
  qemuProcess.destroy()

  info("End")
  System.exit(0)
}
