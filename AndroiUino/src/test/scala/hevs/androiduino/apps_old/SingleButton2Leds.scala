package hevs.androiduino.apps_old

import hevs.androiduino.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.androiduino.dsl.generator.CodeGenerator

object SingleButton2Leds extends TestGeneratorApp {
  val b1 = DigitalInput(3)
  val l1 = DigitalOutput(4)
  val l2 = DigitalOutput(2)

  // Connecting stuff
  b1.out --> l2.in
  b1.out --> l1.in

  /**
   * TODO implement this
   */
  //	HW_Button(5).out --> HW_Led(12).in
  //	HW_Button(5).out --> HW_Led(13).in

  // Generate code
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  println(code)
}