package hevs.especial.genenator

import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}

class Sch1Code extends STM32TestSuite {

  val qemuLoggerEnabled = true

  def getDslCode = {
    // Inputs
    val btn1 = DigitalInput(Stm32stk.pin_btn)
    // val cst1 = Constant(uint1(v = false))

    // Outputs
    val led1 = DigitalOutput(Stm32stk.pin_led)

    // Connecting stuff
    btn1.out --> led1.in
    // cst1.out --> led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = false)

  runCodeGenTest()
}