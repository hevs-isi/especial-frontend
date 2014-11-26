package hevs.especial.genenator

import hevs.especial.dsl.components.core.logic.{And2, And4}
import hevs.especial.dsl.components.core.{Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.{Stm32stk, DigitalOutput}
import hevs.especial.dsl.components.{uint8, Pin, bool}

class Sch5Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {
    // Inputs
    val cst1 = Constant(bool(v = false))
    val cst2 = Constant(bool(v = true))

    // Logic
    val and1 = And2()
    val mux1 = Mux2[bool]()

    // Output
    val led1 = DigitalOutput(Stm32stk.pin_led)

    // Connecting stuff
    cst1.out --> led1.in
    cst2.out --> and1.in1
    and1.out --> mux1.in2
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = true)

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}