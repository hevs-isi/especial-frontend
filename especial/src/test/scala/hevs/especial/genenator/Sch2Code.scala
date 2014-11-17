package hevs.especial.genenator

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.core.logic.And2
import hevs.especial.dsl.components.target.stm32stk.{DigitalOutput, Stm32stk}
import hevs.especial.dsl.components.{Pin, bool}

class Sch2Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def getDslCode = {
    // Inputs
    val cst1 = Constant(bool(v = true))
    val cst2 = Constant(bool(v = true))

    // Logic
    val and1 = And2()

    // Outputs
    val led1 = DigitalOutput(Pin('C', 11))
    val led2 = DigitalOutput(Stm32stk.pin_led)

    // Connecting stuff
    and1.out --> led1.in
    and1.out --> led2.in
    cst1.out --> and1.in1
    cst2.out --> and1.in2
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = false)

  runCodeGenTest()
}