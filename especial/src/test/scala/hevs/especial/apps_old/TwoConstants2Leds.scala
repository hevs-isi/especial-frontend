package hevs.especial.apps_old

import hevs.especial.genenator.STM32TestSuite

//FIXME: use this test
@Deprecated
object TwoConstants2Leds extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def getDslCode = ""

  /* val c1 = Constant(uint1())
  val c2 = Constant(uint1())

  val led1 = DigitalOutput(4)
  val led2 = DigitalOutput(5)

  // Connecting stuff
  c1.out --> led2.in
  c2.out --> led1.in

  // Generate code
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  println(code) */
}