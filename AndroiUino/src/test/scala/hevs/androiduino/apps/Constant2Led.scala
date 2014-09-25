package hevs.androiduino.apps

import hevs.androiduino.dsl.CodeGenerator
import hevs.androiduino.dsl.components.Constant
import hevs.androiduino.dsl.components.HW_Led
import hevs.androiduino.dsl.components.fundamentals.uint1

object Constant2Led extends TestGeneratorApp {

  val c = Constant(uint1(true)) // FIXME error if true, ok if false
  val led = HW_Led(4)

  // Connecting stuff
  c.out --> led.in // FIXME check arguments here before compile with macro ?

  // Generate code
  val code = CodeGenerator.generateCode
  CodeGenerator.outputToFile(s"codeOutput/$fileName.c", code)

  println(code)
}