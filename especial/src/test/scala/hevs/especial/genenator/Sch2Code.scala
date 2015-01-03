package hevs.especial.genenator

import hevs.especial.dsl.components.bool
import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.core.logic.And2
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO

/**
 * The `led1` is ON when the program is running.
 * The `led2` is ON only when the `btn1` is pressed. An [[And2]] gate is used for the demo, but it is useless.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Sch2Code extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  import hevs.especial.dsl.components.ImplicitTypes._

  def runDslCode(): Unit = {
    // Inputs
    val cst1 = Constant[bool](true).out
    val btn1 = Stm32stkIO.btn1.out

    // Logic
    val and1 = And2(cst1, btn1)
    /*
      val and1 = And2()
      cst1.out --> and1.in1
      btn1.out --> and1.in2
    */

    // Outputs
    val led1 = Stm32stkIO.led1.in
    val led2 = Stm32stkIO.led2.in

    // Connecting stuff
    cst1 --> led1
    and1.out --> led2
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}