package hevs.especial.genenator

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.digital.DigitalOutput
import hevs.especial.dsl.components.logic.And2
import hevs.especial.dsl.components.uint1

class Sch2Code extends STM32TestSuite {

  def getDslCode = {
    // Inputs
    val cst1 = Constant(uint1(v = true))
    val cst2 = Constant(uint1(v = true))

    // Logic
    val and1 = And2()

    // Output
    val led1 = DigitalOutput(7)
    val led2 = DigitalOutput(8)

    // Connecting stuff
    and1.out --> led1.in
    and1.out --> led2.in
    cst1.out --> and1.in1
    cst1.out --> and1.in2

    // cst2.out --> and1(3) // Test with 14 // 1
  }

  runDotGeneratorTest()
  runCodeCheckerTest(hasWarnings = true)

  runCodeGenTest()
}