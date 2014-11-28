package hevs.especial.genenator

import hevs.especial.dsl.components.Pin
import hevs.especial.dsl.components.core.TickToggle
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}

class Sch1bCode extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {
    // Input
    val btn1 = DigitalInput(Stm32stk.pin_btn)

    // Generator
    val gen1 = TickToggle(initValue = false)

    // Outputs
    val led1 = DigitalOutput(Stm32stk.pin_led)
    val led2 = DigitalOutput(Pin('C', 0xD))

    // Connecting stuff
    gen1.out --> led1.in
    btn1.out --> led2.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}