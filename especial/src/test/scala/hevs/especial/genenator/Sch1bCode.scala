package hevs.especial.genenator

import hevs.especial.dsl.components.{bool, Pin}
import hevs.especial.dsl.components.core.TickToggle
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}

class Sch1bCode extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {
    // Input
    val btn1 = DigitalInput(Stm32stk.btn0_pin)

    // Generator
    val gen1 = TickToggle(bool(false))

    // Outputs
    val led1 = DigitalOutput(Stm32stk.led0_pin)
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