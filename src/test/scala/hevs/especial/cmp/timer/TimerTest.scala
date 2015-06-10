package hevs.especial.cmp.timer

import hevs.especial.dsl.components.target.stm32stk.{DelayedEvent, Stm32stkIO, Timer}
import hevs.especial.generator.STM32TestSuite

/**
 * Basic timer test.
 * Use periodic and single-shot timers to make LEDs blinking.
 *
 * @version 1.0
 * @author Christopher Métrailler (mei@hevs.ch)
 */
class TimerTest extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {

    import hevs.especial.dsl.components.core.logic._

    import language.{implicitConversions, postfixOps}
    import scala.concurrent.duration._

    // Output
    val led2 = Stm32stkIO.led2

    // Logic
    val toggle1 = ToggleBoolean() // Init output value is off
    val toggle2 = ToggleBoolean()
    val toggle3 = ToggleBoolean(true)

    // TODO: add a function like -|-> to insert a toggle boolean component automatically

    val timer1 = Timer(0.5 second)

    // Connecting stuff

    // Toggle led2 each 500ms, init value is off
    timer1.out --> toggle1.en
    toggle1.out --> led2.in

    // Toggle led1 each 500ms, initial delay of 500ms (led1 is the invert of led2)
    Timer(500 millis, 500 millis).out --> toggle2.en
    toggle2.out --> Stm32stkIO.led1.in

    // Led3 is the invert of led2 (same as led1 using an inverter gate)
    !toggle1.out --> Stm32stkIO.led3.in

    // Switch the led4 off after 4s (initial value is on)
    DelayedEvent(4 seconds).out --> toggle3.en
    toggle3.out --> Stm32stkIO.led4.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest(compile = false)
}
