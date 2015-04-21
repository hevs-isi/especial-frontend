package hevs.especial.simulation

import hevs.especial.dsl.components.bool
import hevs.especial.dsl.components.core.{Constant, TickToggle}
import hevs.especial.dsl.components.target.stm32stk.{Stm32stk, Stm32stkIO}
import hevs.especial.generator.STM32TestSuite

/**
 * QEMU simulation: when the main button is pressed, the `led2` is ON.
 *
 * On each loop iterations, the state of the `led2` is toggle.
 * It toggle very fast (at loop cycle time). Its initial value is ON.
 *
 * This program can be simulated in QEMU (see [[Sch4Simulation]] test case).
 * After 6 loop ticks, output values are the following :
 * {{
 * Pin 'C#03' has 06 values:	1-0-1-0-1-0
 * Pin 'C#04' has 06 values:	0-0-0-0-0-0
 * }}
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Sch4SimCode extends STM32TestSuite {

  // Add QEMU logger events in the generated code
  def isQemuLoggerEnabled = true

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