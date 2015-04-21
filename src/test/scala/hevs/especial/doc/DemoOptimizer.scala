package hevs.especial.doc

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.math.Add2
import hevs.especial.dsl.components.core.{Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.generator.STM32TestSuite

/**
 * Code used for the documentation (report) only.
 *
 * Contains a programs with warnings.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class DemoOptimizer extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {

    val add2 = Add2[bool]()
    val cst1 = Constant(uint8(1)).out
    val cst2 = Constant(bool(true)).out
    val btn1 = Stm32stkIO.btn1.out
    val mux2 = Mux2[bool]()
    val led1 = Stm32stkIO.led1.in

    // Connecting stuff
    cst1 --> mux2.sel
    btn1 --> mux2.in1

    cst2 --> add2.in1
    btn1 --> add2.in2

    mux2.out --> led1

    val pwm4 = Stm32stkIO.pwm4.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = true)

  runCodeOptimizer(hasWarnings = true)

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}