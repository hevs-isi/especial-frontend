package hevs.especial.doc

import hevs.especial.dsl.components.bool
import hevs.especial.dsl.components.core.logic.Not
import hevs.especial.dsl.components.target.stm32stk.{Stm32stk, DigitalOutput, Stm32stkIO}
import hevs.especial.generator.STM32TestSuite

/**
 * Sample application to demonstrates how boolean implicit conversions can be used.
 *
 * Connect a button to a LED which is active low.
 */
class DemoBoolOps extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {

    // This LED is active low. Add a Not component before it.
    val led = {
      val led = DigitalOutput(Stm32stk.led0_pin)
      val invert = Not[bool]()
      invert.out --> led.in
      invert // Not component, not the LED directly
    }
    Stm32stkIO.btn1.out --> led.in

    import hevs.especial.dsl.components.core.logic._
    
    !Stm32stkIO.btn1.out --> Stm32stkIO.led1.in // Same as before, in one line
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
