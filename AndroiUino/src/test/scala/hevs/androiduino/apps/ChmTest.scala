package hevs.androiduino.apps

import hevs.androiduino.dsl.CodeGenerator
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.DigitalOutput
import hevs.androiduino.dsl.components.fundamentals.uint1

object ChmTest extends TestGeneratorApp {
  val c = Constant(uint1(true))
  val led = DigitalOutput(4)

  // Connecting stuff
  c.out --> led.in
  // Will set the Led ON

  // Generate code
  val code = CodeGenerator.generateCode
  CodeGenerator.outputToFile(s"codeOutput/$fileName.c", code)

  println(code)
}