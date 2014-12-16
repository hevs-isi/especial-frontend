package hevs.especial.apps

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.CFct
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.genenator.STM32TestSuite

/**
 * Custom C component to create a simple threshold function.
 */
class CustomThreshold extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {
    val adc1 = Stm32stkIO.adc1
    adc1.out --> Stm32stkIO.pwm3.in

    val threshold = Threshold()
    adc1.out --> threshold.in
    threshold.out --> Stm32stkIO.led1.in
  }

  /**
   * Custom threshold component.
   *
   * Logic implemented in C. The output is `false` when the input value is above the threshold value,
   * and `true` otherwise.
   *
   * @param threshold the threshold of the component
   */
  case class Threshold(threshold: Int = 512) extends CFct[uint16, bool]() {

    /* I/O management */

    def globalVars = {
      // Example: "uint16_t threshold_in = 0;"
      Map("threshold_in" -> uint16(0))
    }

    def setInputValue(s: String): String = s"threshold_in = $s"

    def getOutputValue: String = "threshold_out"

    /* Code generation */

    override def loopCode = {
      val outType = getTypeString[bool]
      s"""
         |$outType threshold_out = false;
         |if(threshold_in > $threshold)
         |  threshold_out = true;
      """.stripMargin
    }
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
