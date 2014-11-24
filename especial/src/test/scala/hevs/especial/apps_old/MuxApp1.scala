package hevs.especial.apps_old

import hevs.especial.genenator.STM32TestSuite

//FIXME: use this test

@Deprecated
object MuxApp1 extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode = {

  }

  /*val c = Constant(uint8())
  val d = Constant(uint8())

  val m = Mux2[uint8](uint8())
  val ext = BitExtractor[uint8]()
  val led = DigitalOutput(5)

  // Connecting stuff
  c.out --> m.in1
  d.out --> m.in2
  m.out --> ext.in1
  ext.out --> led.in

  // Generate code
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  println(code)*/
}