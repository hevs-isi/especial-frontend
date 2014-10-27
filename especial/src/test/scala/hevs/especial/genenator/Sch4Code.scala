package hevs.especial.genenator

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.digital.DigitalOutput
import hevs.especial.dsl.components.fundamentals.uint1
import hevs.especial.dsl.components.logic.{And2, And4}

class Sch4Code extends STM32TestSuite {

  def getDslCode = {
    val cst1 = Constant(uint1(v = false))
    val cst2 = Constant(uint1(v = false))
    val and1 = And2()
    val and2 = And2()
    val and3 = And4()
    val led1 = DigitalOutput(7)

    cst2.out --> and3.in2
    cst1.out --> and1.in1
    and1.out --> and2.in2
    and2.out --> and3.in4
    and3.out --> led1.in
  }

  runDotGeneratorTest()
  runCodeCheckerTest(hasWarnings = true)
}