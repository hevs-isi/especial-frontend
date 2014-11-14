package hevs.especial.genenator

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.core.logic.{And2, And4}
import hevs.especial.dsl.components.target.stm32stk.DigitalOutput
import hevs.especial.dsl.components.{Pin, uint1}

class Sch4Code extends STM32TestSuite {

  val qemuLoggerEnabled = true

  def getDslCode = {
    // Inputs
    val cst1 = Constant(uint1(v = false))
    val cst2 = Constant(uint1(v = false))

    // Logic
    val and1 = And2()
    val and2 = And2()
    val and3 = And4()

    // Output
    val led1 = DigitalOutput(Pin('C', 12))

    // Connecting stuff
    cst2.out --> and3.in2
    cst1.out --> and1.in1
    and1.out --> and2.in2
    and2.out --> and3.in4
    and3.out --> led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = true)

  runCodeGenTest()
}