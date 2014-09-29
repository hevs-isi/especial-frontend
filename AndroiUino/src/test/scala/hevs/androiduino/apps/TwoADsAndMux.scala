package hevs.androiduino.apps

import java.io.PrintWriter

import hevs.androiduino.apps.Constant2Led._
import hevs.androiduino.dsl.CodeGenerator
import hevs.androiduino.dsl.components.digital.{DigitalOutput, DigitalInput}

object TwoADsAndMux extends TestGeneratorApp {
  val b1 = DigitalInput(3)
  val l1 = DigitalOutput(4)
  val l2 = DigitalOutput(2)

  // Connecting stuff
  b1.out --> l2.in
  b1.out --> l1.in

  // Generate code
  val code = CodeGenerator.generateCode
  val writer = new PrintWriter(s"codeOutput/$fileName.c")
  writer.print(code)
  writer.close()
}