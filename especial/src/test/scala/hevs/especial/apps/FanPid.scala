package hevs.especial.apps

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.math.PID
import hevs.especial.dsl.components.core.{Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.{PulseInputCounter, Stm32stkIO}
import hevs.especial.generator.STM32TestSuite
import hevs.especial.pres.{Not, SpeedGain, Threshold}

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
    val pid = PID(1.0, 0.5, 0, 50, 4000)
    val pulse = PulseInputCounter(Pin('B', 9)).out
    val measure = Stm32stkIO.adc1.out

    // Logic
    val speedGain = SpeedGain(4000.0 * 45.0)
    val mux = Mux2[uint16]()
    val not = Not()

    // Output
    val pwm = Stm32stkIO.pwm3

    // PID input measure from the pulse counter
    pulse --> speedGain.in
    speedGain.out --> pid.measure

    // Read setpoint from the potentiometer
    measure --> pid.setpoint

    // Stop the fan using the button
    Constant(uint16(50)).out --> mux.in1
    pid.out --> mux.in2
    Stm32stkIO.btn1.out --> not.in
    not.out --> mux.sel

    // Fan PWM command
    mux.out --> pwm.in

    // TODO: Add threshold
    val trigger = Threshold(4096 / 2)
    mux.out --> trigger.in
    trigger.out --> Stm32stkIO.led1.in
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
