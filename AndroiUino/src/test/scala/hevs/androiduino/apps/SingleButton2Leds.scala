package hevs.androiduino.apps

import hevs.androiduino.dsl.CodeGenerator
import hevs.androiduino.dsl.components.HW_Button
import hevs.androiduino.dsl.components.HW_Led
import hevs.androiduino.dsl.components.fundamentals.ComponentManager

object SingleButton2Leds extends TestGeneratorApp {
  val b1 = HW_Button(3)
  val l1 = HW_Led(4)
  val l2 = HW_Led(2)

  // Connecting stuff
  b1.out --> l2.in
  b1.out --> l1.in

  /**
   * TODO implement this
   */
  //	HW_Button(5).out --> HW_Led(12).in
  //	HW_Button(5).out --> HW_Led(13).in

  // Generate code
  val code = CodeGenerator.generateCode
  CodeGenerator.outputToFile(s"codeOutput/$fileName.c", code)
  println(code)

  println(ComponentManager.gr1)
}