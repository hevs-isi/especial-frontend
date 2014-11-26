package hevs.especial.genenator

import hevs.especial.dsl.components.bool
import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.target.stm32stk.{DigitalOutput, Stm32stk}

class Sch0Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {
    // Input
    val cst1 = Constant(bool(v = true))

    // Output
    val led1 = DigitalOutput(Stm32stk.pin_led)

    // Connecting stuff
    cst1.out --> led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}