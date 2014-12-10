package hevs.especial.genenator

import hevs.especial.dsl.components.core.logic.{And2, And4}
import hevs.especial.dsl.components.core.{Mux3, Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.{Stm32stk, DigitalOutput}
import hevs.especial.dsl.components.{Pin, bool, uint8}

class Sch4Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {
    // Inputs
    val cst1 = Constant(bool(v = false))
    val cst2 = Constant(bool(v = false))

    // Logic
    val and1 = And4()
    val and2 = And2()
    val and3 = And2()
    val mux1 = Mux2[bool]()

    // Output
    val led1 = DigitalOutput(Stm32stk.led0_pin)

    // Connecting stuff
    cst2.out --> mux1.in1
    cst1.out --> and1.in1
    and3.out --> and1.in4
    and1.out --> and2.in2
    and2.out --> mux1.in2
    mux1.out --> led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = true)

  runCodeOptimizer(hasWarnings = true) // Still some unconnected pins

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}