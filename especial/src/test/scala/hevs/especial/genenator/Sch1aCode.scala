package hevs.especial.genenator

import hevs.especial.dsl.components.Pin
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}

/**
 * Create anonymous digital I/O.
 */
class Sch1aCode extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {

    // Button to the same LED
    //val btn1 = DigitalInput(Stm32stk.pin_btn)
    //btn1.out --> DigitalOutput(Pin('A', 5)).in
    //btn1.out --> DigitalOutput(Pin('A', 5)).in

    // Same button to 2 LEDs
    val led1 = DigitalOutput(Stm32stk.led0_pin).in
    val led2 = DigitalOutput(Pin('B', 6)).in

    DigitalInput(Stm32stk.btn0_pin).out --> led1
    DigitalInput(Stm32stk.btn0_pin).out --> led2
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}