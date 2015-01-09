package hevs.especial.generator

import hevs.especial.dsl.components.bool
import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO

/**
 * Switch the `led1` to ON using a constant value.
 * Use the extension board.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Sch0Code extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {
    // Input
    val cst1 = Constant(bool(true))

    // Output
    val led1 = Stm32stkIO.led1

    // Connecting stuff
    cst1.out --> led1.in

    // Same as :
    // Constant(true).out --> Stm32stk.led0.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}