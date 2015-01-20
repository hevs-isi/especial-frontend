package hevs.especial.doc

import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.generator.STM32TestSuite

/**
 * Draw a simple dot diagram for the documentation with 4 components only.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class DotSch extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {
    import hevs.especial.dsl.components.core.logic._

    (Stm32stkIO.btn1.out & Stm32stkIO.btn2.out) --> Stm32stkIO.led1.in
  }

  runDotGeneratorTest()
}
