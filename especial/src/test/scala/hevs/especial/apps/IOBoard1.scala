package hevs.especial.apps

import hevs.especial.dsl.components.bool
import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.target.stm32stk.{Stm32stk, Stm32stkIO}
import hevs.especial.genenator.STM32TestSuite

/**
 * Sample application used to test the I/O extension board.
 */
class IOBoard1 extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {

    // Main LED is ON
    Constant(bool(v = true)).out --> Stm32stk.led0.in

    // Control extensions LEDs using the corresponding button
    Stm32stkIO.btn2.out --> Stm32stkIO.led2.in
    Stm32stkIO.btn3.out --> Stm32stkIO.led3.in
    Stm32stkIO.btn4.out --> Stm32stkIO.led4.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
