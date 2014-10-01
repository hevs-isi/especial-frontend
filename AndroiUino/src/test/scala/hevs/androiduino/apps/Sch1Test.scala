package hevs.androiduino.apps

import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.generator.CodeGenerator

object Sch1Code {

  // Inputs
  val btn1 = DigitalInput(4)
  val cst1 = Constant(uint1(true))

  // Outputs
  val led1 = DigitalOutput(7)
  val led2 = DigitalOutput(8)
  val led3 = DigitalOutput(9)

  // Connecting stuff
  btn1.out --> led1.in
  btn1.out --> led2.in

  cst1.out --> led3.in
}

object Sch1Test extends TestGeneratorApp {
  // Generate code
  val source = Sch1Code
  val code = CodeGenerator.generateCode(fileName)
  CodeGenerator.outputToFile(s"codeOutput/$fileName.c", code)

  println(code)
}