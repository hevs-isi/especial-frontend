package hevs.especial.simulation

import hevs.especial.dsl.components.core.{Constant, Mux2, TickToggle}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.dsl.components.{bool, uint8}
import hevs.especial.generator.STM32TestSuite

/**
 * Test case for the QEMU simulation.
 * Use [[TickToggle]] generator and a [[Mux2]] to produce output values to two LEDs.
 *
 * This program can be simulated in QEMU (see [[Sch6Simulation]] test case).
 * After 6 loop ticks, output values are the following :
 * {{
 * Pin 'C#03' has 06 values:	1-0-1-0-1-0
 * Pin 'C#04' has 06 values:	1-1-1-1-1-1
 * }}
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Sch6SimCode extends STM32TestSuite {

  def isQemuLoggerEnabled = true

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