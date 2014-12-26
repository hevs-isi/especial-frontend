package hevs.especial.apps

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.core.logic.Not
import hevs.especial.dsl.components.core.math.{Add2, Div2, Mul2, Sub2}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.dsl.components.{bool, uint16}
import hevs.especial.genenator.STM32TestSuite

/**
 * Use math blocks to compute different PWM duty cycles.
 * Generate different PWM duty cycles on LEDs 3 and 4 using math operations from constant values.
 */
class MathPWM extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  import hevs.especial.dsl.components.ImplicitTypes._

  def runDslCode(): Unit = {
    // Outputs
    val pwm3 = Stm32stkIO.pwm3.in
    val pwm4 = Stm32stkIO.pwm4.in

    // Input
    val cst1 = Constant[uint16](4096).out
    val cst2 = Constant[uint16](2).out
    val cst3 = Constant[uint16](4).out
    val cst4 = Constant[uint16](2044).out
    val cst5 = Constant[uint16](512).out

    // Logic

    /*
      val tmp1 = Div2()
      cst1.out --> tmp1.in1
      cst2.out --> tmp1.in2
    */

    val tmp1 = Div2(cst1, cst2)
    val tmp2 = Div2(cst1, cst3)
    val tmp3 = Add2(tmp1.out, cst4)
    val tmp4 = Sub2(cst5, tmp2.out)
    val tmp5 = Mul2(tmp3.out, cst5)

    tmp5.out --> pwm4 // PWM period = 4096 (100.0% duty)
    tmp4.out --> pwm3 // PWM period = 1536 (037.5% duty)

    val cst = Constant[bool](false).out

    val not = Not(cst)

    not.out --> Stm32stkIO.led2.in
    Constant[bool](true).out --> Stm32stkIO.led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
