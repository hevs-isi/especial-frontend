package hevs.especial.genenator

import hevs.especial.dsl.components.bool
import hevs.especial.dsl.components.core.logic.Not
import hevs.especial.dsl.components.core.{Constant, TickToggle}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO

// TODO: comment + test + VCD export

class Sch5SimCode extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  import hevs.especial.dsl.components.ImplicitTypes._

  def runDslCode(): Unit = {
    // Inputs
    val cst1 = Constant[bool](false).out
    val gen1 = TickToggle[bool]()

    // Logic
    val not1 = Not[bool]()

    // Output
    val led1 = Stm32stkIO.led1.in
    val led2 = Stm32stkIO.led2.in

    // Connecting stuff
    cst1 --> gen1.in
    gen1.out --> led1
    gen1.out --> not1.in
    not1.out --> led2
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = false)

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
