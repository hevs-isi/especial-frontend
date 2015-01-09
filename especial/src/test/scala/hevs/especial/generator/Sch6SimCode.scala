package hevs.especial.generator

import hevs.especial.dsl.components.core.{Constant, Mux2, TickToggle}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.dsl.components.{bool, uint8}

/**
 * Sample application using tick generators and a Mux.
 */

// TODO: comment + test + VCD export

class Sch6SimCode extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  // TODO: add variadic constructor for MUX

  import hevs.especial.dsl.components.CType.Implicits._

  def runDslCode(): Unit = {
    // Inputs
    val cst1 = Constant[bool](true).out
    val cst2 = Constant[bool](false).out
    val cst3 = Constant(uint8(0)).out

    val gen1 = TickToggle(cst2).out
    val gen2 = TickToggle(cst3).out

    // Logic
    val mux1 = Mux2[bool]()

    // Output
    val led1 = Stm32stkIO.led1
    val led2 = Stm32stkIO.led2

    // Connecting stuff
    cst1 --> mux1.in1
    gen1 --> mux1.in2
    gen2 --> mux1.sel

    gen1 --> led1.in
    mux1.out --> led2.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = false)

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}