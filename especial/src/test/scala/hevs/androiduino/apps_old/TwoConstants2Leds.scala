package hevs.androiduino.apps_old

import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.DigitalOutput
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.generator.CodeGenerator

object TwoConstants2Leds extends TestGeneratorApp {
  val c1 = Constant(uint1())
  val c2 = Constant(uint1())

  val led1 = DigitalOutput(4)
  val led2 = DigitalOutput(5)

  // Connecting stuff
  c1.out --> led2.in
  c2.out --> led1.in

  // Generate code
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  println(code)
}