package hevs.especial.generator

import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, Stm32stkIO}

/**
 * Command `led1` and `led2` using the same button `btn1`.
 * The two LEDs are switched ON when the button is pressed.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Sch1Code extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  // FIXME: check this with a constrained graph

  def runDslCode(): Unit = {
    val led1 = Stm32stkIO.led1.in
    val led2 = Stm32stkIO.led2.in

    // Inputs buttons are on the same pin.
    // Only one button will be "created" in the graph.
    DigitalInput(Stm32stkIO.btn1_pin).out --> led1
    DigitalInput(Stm32stkIO.btn1_pin).out --> led2
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}