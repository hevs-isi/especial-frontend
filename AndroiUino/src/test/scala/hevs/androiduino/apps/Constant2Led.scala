package hevs.androiduino.apps

import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.DigitalOutput
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.generator.CodeGenerator

object Constant2Led extends TestGeneratorApp {

  val c = Constant(uint1(false))
  val d = Constant(uint1(true))
  val e = Constant(uint1(false))
  val led = DigitalOutput(4)

  // Connecting stuff
  // c.out --> led.in
  // d.out --> led.in

  // Generate code
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  println(code)
}