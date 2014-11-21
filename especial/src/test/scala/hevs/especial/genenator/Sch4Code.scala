package hevs.especial.genenator

import hevs.especial.dsl.components.core.{Mux2, Constant}
import hevs.especial.dsl.components.core.logic.{And2, And4}
import hevs.especial.dsl.components.target.stm32stk.DigitalOutput
import hevs.especial.dsl.components.{Pin, bool}

class Sch4Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def getDslCode = {
    // Inputs
    val cst1 = Constant(bool(v = false))
    val cst2 = Constant(bool(v = false))

    // Logic
    val and1 = And4()
    val and2 = And2()
    val mux1 = Mux2[bool]()

    // Output
    val led1 = DigitalOutput(Pin('C', 12))

    // Connecting stuff
    cst2.out --> mux1.in1
    cst1.out --> and1.in1
    and1.out --> and2.in2
    and2.out --> mux1.in2
    mux1.out --> led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = true)

  runCodeGenTest()
}