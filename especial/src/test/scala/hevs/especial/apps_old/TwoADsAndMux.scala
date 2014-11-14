package hevs.especial.apps_old

import hevs.especial.genenator.STM32TestSuite

//FIXME: use this test
@Deprecated
object TwoADsAndMux extends STM32TestSuite {

  override val qemuLoggerEnabled = false

  def getDslCode = ""

  /*val b1 = DigitalInput(3)
  val l1 = DigitalOutput(4)
  val l2 = DigitalOutput(2)

  // Connecting stuff
  b1.out --> l2.in
  b1.out --> l1.in

  // Generate code
  val code = CodeGenerator.generateCode(fileName)
  val writer = new PrintWriter(s"codeOutput/$fileName.c")
  writer.print(code)
  writer.close()*/
}