package hevs.especial.apps

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.core.math.{Mul2, Add2, Div2, Sub2}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.dsl.components.{bool, uint16}
import hevs.especial.genenator.STM32TestSuite

/**
 * Use simple math blocks to create different PWM duty cycles.
 *
 * Generate different PWM duty cycles on LEDs 3 and 4 using math blocks.
 */
class MathPWM extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {

    import hevs.especial.dsl.components.ImplicitTypes._

    // Outputs
    val pwm3 = Stm32stkIO.pwm3.in
    val pwm4 = Stm32stkIO.pwm4.in

    // Input
    val cst1 = Constant[uint16](4096)
    val cst2 = Constant[uint16](2)
    val cst3 = Constant[uint16](4)
    val cst4 = Constant[uint16](2044)
    val cst5 = Constant[uint16](512)

    // Logic
    val tmp1 = Div2[uint16]()
    cst1.out --> tmp1.in1
    cst2.out --> tmp1.in2

    val tmp2 = Div2[uint16]()
    cst1.out --> tmp2.in1
    cst3.out --> tmp2.in2

    val tmp3 = Add2[uint16]()
    tmp1.out --> tmp3.in1
    cst4.out --> tmp3.in2

    val tmp4 = Sub2[uint16]()
    tmp2.out --> tmp4.in1
    cst5.out --> tmp4.in2

    val tmp5 = Mul2[uint16]()
    tmp3.out --> tmp5.in1
    cst5.out --> tmp5.in2

    tmp5.out --> pwm4 // PWM period = 4096 (100.0% duty)
    tmp4.out --> pwm3 // PWM period = 1536 (037.5% duty)

    Constant[bool](true).out --> Stm32stkIO.led2.in
    Constant[bool](true).out --> Stm32stkIO.led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
