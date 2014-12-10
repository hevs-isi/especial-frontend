package hevs.especial.genenator

import hevs.especial.dsl.components.core.logic.{And2, And4}
import hevs.especial.dsl.components.core.{TickToggle, Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.{Stm32stk, DigitalOutput}
import hevs.especial.dsl.components.{uint8, Pin, bool}

class Sch6Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {
    // Inputs
    val cst1 = Constant(bool(true))
    val gen1 = TickToggle(bool(true))

    val gen2 = TickToggle(uint8(1))

    // Logic
    val mux1 = Mux2[bool]()

    // Output
    val led1 = DigitalOutput(Pin('C', 12))
    val led2 = DigitalOutput(Pin('C', 13))

    // Connecting stuff
    cst1.out --> mux1.in1
    gen1.out --> mux1.in2
    gen2.out --> mux1.sel

    gen1.out --> led1.in
    mux1.out --> led2.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = false)

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}