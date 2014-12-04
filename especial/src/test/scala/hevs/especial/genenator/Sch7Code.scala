package hevs.especial.genenator

import hevs.especial.dsl.components.core.TickToggle
import hevs.especial.dsl.components.core.logic.Not
import hevs.especial.dsl.components.target.stm32stk.DigitalOutput
import hevs.especial.dsl.components.{Pin, bool}

class Sch7Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {
    // Inputs
    val gen1 = TickToggle(bool(true))

    // Logic
    val not1 = Not[bool]()

    // Output
    val led1 = DigitalOutput(Pin('C', 12))
    val led2 = DigitalOutput(Pin('C', 13))

    // Connecting stuff
    gen1.out --> led1.in
    gen1.out --> not1.in
    not1.out --> led2.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = false)

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()

}
