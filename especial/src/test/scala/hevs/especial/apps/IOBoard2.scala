package hevs.especial.apps

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.dsl.components.{bool, uint16}
import hevs.especial.genenator.STM32TestSuite

/**
 * Sample application used to test the I/O extension board.
 *
 * The `led1` is switched ON.
 * The `btn2` controls the `led2`.
 * The potentiometer control the `led3` using a PWM output.
 * A constant value is set to the `led4` using a PWM output.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class IOBoard2 extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {
    Constant(bool(true)).out --> Stm32stkIO.led1.in
    Stm32stkIO.btn2.out --> Stm32stkIO.led2.in

    Stm32stkIO.adc1.out --> Stm32stkIO.pwm3.in
    Constant(uint16(1024)).out --> Stm32stkIO.pwm4.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
