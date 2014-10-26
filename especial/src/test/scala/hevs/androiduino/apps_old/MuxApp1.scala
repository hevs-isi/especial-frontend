package hevs.androiduino.apps_old

import hevs.androiduino.dsl.components.BitExtractor
import hevs.androiduino.dsl.components.core.{Constant, Mux2}
import hevs.androiduino.dsl.components.digital.DigitalOutput
import hevs.androiduino.dsl.components.fundamentals.uint8
import hevs.androiduino.dsl.generator.CodeGenerator

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
  // Generate code
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  println(code)
}