package hevs.especial.doc

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.logic._
import hevs.especial.dsl.components.core.{Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.generator.STM32TestSuite
import hevs.especial.pres.Not

/**
 * Simple application to demonstrate how the [[hevs.especial.generator.Resolver]] block works.
 *
 * Read a button, invert its state and convert it value to an [[uint8]] to control the selection pin of a [[Mux2]].
 * The `led1` is always ON. When the `btn1` is pressed, the `led2` switch OFF. Application without warnings.
 *
 * @version 1.0
 * @author Christopher Métrailler (mei@hevs.ch)
 */
class DemoResolver extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {
    val not = Not() // Not and `uint8` conversion
    Stm32stkIO.btn1.out --> not.in

    val mux = Mux2[bool]()
    val cst1 = Constant(bool(true)).out
    mux.out --> Stm32stkIO.led1.in // Update outputs
    cst1 --> Stm32stkIO.led2.in

    not.out --> mux.sel
    !cst1 --> mux.in2
    cst1 --> mux.in1
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}