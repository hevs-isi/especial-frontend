package hevs.androiduino.apps

import hevs.androiduino.dsl.CodeGenerator
import hevs.androiduino.dsl.components.Constant
import hevs.androiduino.dsl.components.HW_Led
import hevs.androiduino.dsl.components.fundamentals.uint1

object TwoConstant2Leds extends TestGeneratorApp {
  val c1 = Constant(uint1())
  val c2 = Constant(uint1())

  val led1 = HW_Led(4)
  val led2 = HW_Led(5)

  // Connecting stuff
  c1.out --> led2.in
  c2.out --> led1.in

  // Generate code
  val code = CodeGenerator.generateCode
  CodeGenerator.outputToFile(s"codeOutput/$appName.c", code)

  println(code)
}