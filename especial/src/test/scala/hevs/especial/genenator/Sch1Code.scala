package hevs.especial.genenator

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.especial.dsl.components.fundamentals.uint1
import hevs.especial.dsl.components.target.Stm32stk

class Sch1Code extends STM32TestSuite {

  def getDslCode = {

    new Stm32stk()

    // Inputs
    //val btn1 = DigitalInput(4)
    val cst1 = Constant(uint1(v = true))

    //val led4 = DigitalOutput(42) // NC

    // Outputs
    val led1 = DigitalOutput(12)
    //val led2 = DigitalOutput(8)
    //val led3 = DigitalOutput(9)

    // Connecting stuff
    //btn1.out --> led1.in
    //btn1.out --> led2.in

    cst1.out --> led1.in
  }

  runDotGeneratorTest()
  runCodeCheckerTest(hasWarnings = false)

  runCodeGenTest()
}