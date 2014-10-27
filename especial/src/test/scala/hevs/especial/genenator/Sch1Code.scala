package hevs.especial.genenator

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.especial.dsl.components.fundamentals.uint1

class Sch1Code extends STM32TestSuite {

  def getDslCode = {
    // Inputs
    val btn1 = DigitalInput(4)
    val cst1 = Constant(uint1(true))

    val led4 = DigitalOutput(42) // NC

    // Outputs
    val led1 = DigitalOutput(7)
    val led2 = DigitalOutput(8)
    val led3 = DigitalOutput(9)

    // Connecting stuff
    btn1.out --> led1.in
    btn1.out --> led2.in

    cst1.out --> led3.in
  }

  runDotTest()
}