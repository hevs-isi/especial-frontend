package hevs.especial.compiler

import java.io.File

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.digital.DigitalOutput
import hevs.especial.dsl.components.fundamentals.uint1
import hevs.especial.generator.{Resolver, CodeGenerator}
import hevs.especial.simulation.MonitorServer
import hevs.especial.utils.{Context, OSUtils, RichFile}

/**
 * Path example from a program to the QEMU simulation.
 */
object CompilerPathTest extends App with Logging {

  //FIXME: use pipeline to do this. Interrupt if any error occurs
  class Code {
    val cst1 = Constant(uint1(v = true))
    val led1 = DigitalOutput(7)
    cst1.out --> led1.in
  }

  val code = new Code

  val ctx = new Context("Code")
  val resolve = new Resolver().run(ctx)("")
  val c = new CodeGenerator().run(ctx)(resolve)

  //val f: RichFile = new File("csrc/src/main.c")

  // Must be in "/usr/bin/"
  // arm-none-eabi-size
  // arm-none-eabi-gcc
  // arm-none-eabi-g++
  // arm-none-eabi-objcopy
  info("Compiling...")
  val makeRes = OSUtils.runWithCodeResult("/usr/bin/make -r -j4 -C csrc/target-qemu/ all")
  // f.write(c) // TODO: write the generated code to the file

  if (makeRes._1 != 0) {
    error("Make error !")
    error(makeRes._2)
  }

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
