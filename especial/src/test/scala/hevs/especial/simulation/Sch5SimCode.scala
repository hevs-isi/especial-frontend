package hevs.especial.simulation

import hevs.especial.dsl.components.bool
import hevs.especial.dsl.components.core.logic.Not
import hevs.especial.dsl.components.core.{TickToggle, Constant}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.generator.STM32TestSuite

/**
 * Test case for the QEMU simulation.
 * A [[TickToggle]] generator is used to produce output values to two LEDs.
 *
 * This program can be simulated in QEMU (see [[Sch5Simulation]] test case).
 * After 6 loop ticks, output values are the following :
 * {{
 * Pin 'C#03' has 06 values:	1-0-1-0-1-0
 * Pin 'C#04' has 06 values:	0-1-0-1-0-1
 * }}
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Sch5SimCode extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  import hevs.especial.dsl.components.CType.Implicits._

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
