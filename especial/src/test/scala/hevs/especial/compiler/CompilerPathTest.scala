package hevs.especial.compiler

import grizzled.slf4j.Logging
import hevs.especial.simulation.MonitorServer
import hevs.especial.utils.OSUtils

/**
 * Path example from a program to the QEMU simulation.
 */
object CompilerPathTest extends App with Logging {

  info("Start QEMU in a new process...")

  // Launch in graphic mode
  final var runQemu = "./../../stm32/qemu_stm32/arm-softmmu/qemu-system-arm -M stm32-p103 -kernel " +
    "csrc/target-qemu/csrc.elf -serial null -monitor null"
  val qemuProcess = OSUtils.runInBackground(runQemu)

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
