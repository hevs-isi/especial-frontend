package hevs.especial.genenator

import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}

class Sch1Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {
    // Input
    val btn1 = DigitalInput(Stm32stk.pin_btn)

    // Output
    val led1 = DigitalOutput(Stm32stk.pin_led)

    // Connecting stuff
    btn1.out --> led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = false)

  runCodeGenTest()
}