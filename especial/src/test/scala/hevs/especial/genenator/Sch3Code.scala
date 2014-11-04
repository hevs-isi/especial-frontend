package hevs.especial.genenator

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.especial.dsl.components.logic.And2
import hevs.especial.dsl.components.uint1

class Sch3Code extends STM32TestSuite {

  def getDslCode = {
    // Inputs
    val cst1 = Constant(uint1(v = true))
    val btn1 = DigitalInput(4)

    // Logic
    val and1 = And2()

    // Output
    val led1 = DigitalOutput(7)

    // Connecting stuff
    and1.out --> led1.in
    cst1.out --> and1.in1
    btn1.out --> and1.in2
  }

  runDotGeneratorTest()
  runCodeCheckerTest(hasWarnings = false)

  runCodeGenTest()
}