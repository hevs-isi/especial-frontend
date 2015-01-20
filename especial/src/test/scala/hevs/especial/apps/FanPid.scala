package hevs.especial.apps

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.math.PID
import hevs.especial.dsl.components.core.{CFct, Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.{PulseInputCounter, Stm32stkIO}
import hevs.especial.generator.STM32TestSuite

/**
 * Complete demo application to control a fan using a PWM controller.
 *
 * The speed of the fan is measure using a pulse counter. Pulses are captured and counted using external interrupts.
 * When the button 1 is pressed, the fan is off. Be default, the fan speed is regulated by a PID controller.
 * The seed setpoint can be adjusted using the potentiometer, from 0 to 100% speed.
 * A custom math block is used to adapt the number of counted pulses to the desired fan speed.
 * PID kp, ki and kd constants are fixed when the program starts, but they could be modified when running.
 *
 * To run this demo, the fan must be connected correctly, and the jumper must connect the fan output (not the led).
 * The block diagram is generated automatically using the [[hevs.especial.generator.DotGenerator]].
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class FanPid extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {

    // Inputs
    val pid = PID(1, 1, 0, 255, 4095)
    val pulse = PulseInputCounter(Pin('B', 9)).out
    val measure = Stm32stkIO.adc1.out

    // Logic
    val speedGain = SpeedGain(50)
    val mux = Mux2[uint16]()
    val not = Not()

    // Output
    val pwm = Stm32stkIO.pwm3

    // PID input measure from the pulse counter
    pulse --> speedGain.in
    speedGain.out --> pid.measure

    // PID input setpoint from the potentiometer
    measure --> pid.setpoint

    // Mux logic to stop the fan using the button 1
    Constant(uint16(50)).out --> mux.in1
    pid.out --> mux.in2
    Stm32stkIO.btn1.out --> not.in
    not.out --> mux.sel

    // Fan PWM command
    mux.out --> pwm.in
  }

  /**
   * Math block used to adapt the speed of the fan.
   * @param gain speed gain from measure
   */
  case class SpeedGain(gain: Int) extends CFct[uint32, int32]() {

    override val description = "Custom gain"

    /* I/O management */
    private val outVal = outValName()

    override def getOutputValue = outVal

    /* Code generation */
    override def loopCode = {
      val outType = getTypeString[int32]
      val in = getInputValue
      s"""
       |$outType $outVal = 4096 - (($in - 110) * $gain); // Speed gain
       |if ($outVal <= 0)
       |  $outVal = 0;
      """.stripMargin
    }
  }

  /** Custom C component to invert a [[bool]] value and return an [[uint8]] value. */
  case class Not() extends CFct[bool, uint8]() {

    override val description = "Inverter to uint8"

    /* I/O management */
    private val outVal = outValName()

    override def getOutputValue = outVal

    /* Code generation */
    override def loopCode = {
      val outType = getTypeString[uint8]
      val in = getInputValue
      s"$outType $outVal = ($in == 0) ? 1 : 0;"
    }
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
