package hevs.androiduino.apps

import hevs.androiduino.dsl.CodeGenerator
import hevs.androiduino.dsl.components.core.{Mux2, Constant}
import hevs.androiduino.dsl.components.digital.DigitalOutput
import hevs.androiduino.dsl.components.BitExtractor
import hevs.androiduino.dsl.components.fundamentals.{ComponentManager, uint8}

object MuxApp1 extends TestGeneratorApp {
  val c = Constant(uint8())
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
  val code = CodeGenerator.generateCode
  CodeGenerator.outputToFile(s"codeOutput/$fileName.c", code)

  //	println(code)
  println(ComponentManager.gr1)
}