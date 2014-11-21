package hevs.especial.genenator

import hevs.especial.dsl.components.{Pin, bool}
import hevs.especial.dsl.components.core.{Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}

class Sch3Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def getDslCode = {
    // Inputs
    val cst1 = Constant(bool(v = true))
    val cst2 = Constant(bool(v = false))
    val btn1 = DigitalInput(Stm32stk.pin_btn)

    // Logic
    val mux2 = Mux2[bool]()

    // Output
    val led1 = DigitalOutput(Stm32stk.pin_led)
    val led2 = DigitalOutput(Pin('C', 0xD))

    // Connecting stuff
    cst1.out --> mux2.in1
    cst2.out --> mux2.in2
    btn1.out --> mux2.sel

    mux2.out --> led1.in
    mux2.out --> led2.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = false)

  runCodeGenTest()
}