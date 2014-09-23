package hevs.androiduino.apps

import java.io.PrintWriter

import hevs.androiduino.dsl.CodeGenerator
import hevs.androiduino.dsl.components.HW_Button
import hevs.androiduino.dsl.components.HW_Led

object TwoADsAndMux extends TestGeneratorApp {
  val b1 = HW_Button(3)
  val l1 = HW_Led(4)
  val l2 = HW_Led(2)

  // Connecting stuff
  b1.out --> l2.in
  b1.out --> l1.in

  // Generate code
  val code = CodeGenerator.generateCode
  val writer = new PrintWriter(s"codeOutput/$appName.c")
  writer.print(code)
  writer.close()
}