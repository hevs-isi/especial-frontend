package hevs.androiduino.apps

import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.digital.{DigitalOutput, DigitalInput}
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
  val code = CodeGenerator.generateCode(fileName)
  CodeGenerator.outputToFile(s"codeOutput/$fileName.c", code)
  println(code)

  println(ComponentManager.cpGraph)
}