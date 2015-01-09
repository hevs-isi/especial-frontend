package hevs.especial.generator

import hevs.especial.dsl.components.bool
import hevs.especial.dsl.components.core.{Constant, TickToggle}
import hevs.especial.dsl.components.target.stm32stk.{Stm32stk, Stm32stkIO}

/**
 * When the main button is pressed, the `led2` is ON.
 *
 * On each loop iterations, the state of the `led2` is toggle.
 * It toggle very fast (at loop cycle time). Its initial value is ON.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */

// TODO: comment + test + VCD export

class Sch4SimCode extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  import hevs.especial.dsl.components.CType.Implicits._

  def runDslCode(): Unit = {
    // Input
    val btn0 = Stm32stk.btn0

    // Generator (toggle output on each loop iteration)
    val gen1 = TickToggle[bool]()
    Constant[bool](false).out --> gen1.in

    // Outputs
    val led1 = Stm32stkIO.led1
    val led2 = Stm32stkIO.led2

    // Connecting stuff
    gen1.out --> led1.in
    btn0.out --> led2.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}