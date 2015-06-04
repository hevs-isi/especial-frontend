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

    import language.{implicitConversions, postfixOps}
    import scala.concurrent.duration._

    // Output
    val led1 = Stm32stkIO.led1

    // Logic
    val timer1 = Timer(0.5 second) // 500ms period, no delay

    // Connecting stuff
    timer1.out --> led1.in
    Timer(500 millis, 500 millis).out --> Stm32stkIO.led2.in
    DelayedEvent(4 seconds).out --> Stm32stkIO.led3.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
