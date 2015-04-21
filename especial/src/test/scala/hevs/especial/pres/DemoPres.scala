package hevs.especial.pres

import hevs.especial.dsl.components.{Pin, bool}
import hevs.especial.dsl.components.core.{Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, Stm32stkIO}
import hevs.especial.generator.STM32TestSuite
import hevs.especial.dsl.components.core.logic._

/**
 * Sample dataflow application.
 *
 * @version 1.0
 * @author Christopher Métrailler (mei@hevs.ch)
 */
class DemoPres extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {

    // Components
    val not = Not()
    val mux = Mux2[bool]()
    val cst1 = Constant(bool(true)).out

    // I/O
    val btn1 = DigitalInput(Pin('C', 0))
    val led1 = Stm32stkIO.led1
    val led2 = Stm32stkIO.led2

    // TODO: demo - connecting stuff
    cst1 --> led2.in
    cst1 --> mux.in1
    !cst1 --> mux.in2

    btn1.out --> not.in
    not.out --> mux.sel

    mux.out --> led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest(compile = false)
}