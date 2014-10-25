package hevs.androiduino.compiler

import java.io.File

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.DigitalOutput
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.generator.{RichFile, CodeGenerator}
import hevs.androiduino.dsl.utils.OSUtils
import hevs.especial.simulation.MonitorServer

// From a program to a compiled code
object CompilerTest extends App with Logging {

  class Code {
    val cst1 = Constant(uint1(v = true))
    val led1 = DigitalOutput(7)
    cst1.out --> led1.in
  }

  val code = new Code
  val c = CodeGenerator.generateCode("code")
  val f: RichFile = new File("csrc/src/main.c")
  // f.write(c)

  // Must be in "/usr/bin/"
  // arm-none-eabi-size
  // arm-none-eabi-gcc
  // arm-none-eabi-g++
  // arm-none-eabi-objcopy
  info("Compiling...")
  val makeRes = OSUtils.runWithCodeResult("/usr/bin/make -C csrc/target-qemu/ all")
  if(makeRes._1 != 0) {
    error("Make error !")
    error(makeRes._2)
  }

  // FIXME: how to launch in a new process ?
  info("Start QEMU in a new process...")
  final var runQemu = "./../../stm32/qemu_stm32/arm-softmmu/qemu-system-arm -M stm32-p103 -kernel " +
    "csrc/target-qemu/csrc.elf -serial null -nographic -monitor null &"
  val qemuRes = OSUtils.runWithCodeResult(runQemu)
  println(qemuRes)

  // Monitor
  info("Start monitor. Wait for client...")
  val ms = new MonitorServer(14001)
  val m = ms.waitForClient()

  println("OK !")

  ms.close()
  System.exit(0)
}
