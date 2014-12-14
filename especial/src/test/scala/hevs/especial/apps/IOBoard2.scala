package hevs.especial.apps

import hevs.especial.dsl.components.{uint16, bool}
import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.target.stm32stk.{Stm32stk, Stm32stkIO}
import hevs.especial.genenator.STM32TestSuite

/**
 * Sample application used to test the I/O extension board.
 *
 * Led0 and Led1 are `ON`. The button 2 control its corresponding led. The potentiometer control the led number 3. A
 * constant value is set to the led3 using a PWM.
 */
class IOBoard2 extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {

    Constant(bool(v = true)).out --> Stm32stk.led0.in
    Constant(bool(v = true)).out --> Stm32stkIO.led1.in

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
