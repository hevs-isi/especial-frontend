package hevs.androiduino.apps

import hevs.androiduino.dsl.CodeGenerator
import hevs.androiduino.dsl.components.Constant
import hevs.androiduino.dsl.components.HW_Led
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.Constant

object ChmTest extends TestGeneratorApp {
  val c = Constant(uint1())
  val d = Constant
  val led = HW_Led(4)

  // Connecting stuff
  c.out --> led.in

  // Generate code
  val code = CodeGenerator.generateCode
  CodeGenerator.outputToFile(s"codeOutput/$appName.c", code)

  println(code)
}