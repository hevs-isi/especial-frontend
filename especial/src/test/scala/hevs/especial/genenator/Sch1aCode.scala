package hevs.especial.genenator

import hevs.especial.dsl.components.Pin
import hevs.especial.dsl.components.core.TickToggle
import hevs.especial.dsl.components.target.stm32stk.{DigitalIO, DigitalInput, DigitalOutput, Stm32stk}

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
    val led1 = DigitalOutput(Pin('B', 5)).in
    val led2 = DigitalOutput(Pin('B', 6)).in

    DigitalInput(Pin('A', 1)).out --> led1
    DigitalInput(Pin('A', 1)).out --> led2
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}