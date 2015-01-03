package hevs.especial.apps

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.CFct
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.genenator.STM32TestSuite

/**
 * Sample application using a custom C component and the extension board.
 *
 * Read the analog input value of the potentiometer.
 * If its value is bigger than the threshold, the `led1` si ON, otherwise OFF.
 * The analog value of the potentiometer is displayed using a PWM output on `led3`.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class CustomThreshold extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {
    val adc1 = Stm32stkIO.adc1.out
    adc1 --> Stm32stkIO.pwm3.in

    val threshold = Threshold(512)
    adc1 --> threshold.in
    threshold.out --> Stm32stkIO.led1.in
  }

  /**
   * Custom threshold component.
   *
   * Logic implemented in C. The output is `false` when the input value is above the threshold value,
   * and `true` otherwise (this is not a Schmitt trigger).
   *
   * @param threshold the threshold of the component (between 0x0 and 0xFFFF)
   */
  case class Threshold(threshold: Int = 512) extends CFct[uint16, bool]() {

    /* I/O management */
    private val outVal = valName("threshold")

    override def getOutputValue: String = s"$outVal"

    /* Code generation */
    override def loopCode = {
      val outType = getTypeString[bool]
      val in = getInputValue
      s"""
       |$outType $outVal = false;
       |if($in > $threshold)
       |  $outVal = true;
    """.stripMargin
    }
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
