package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.core.logic.Not
import hevs.especial.dsl.components._

/**
 * STM32-103STK board.
 *
 * https://www.olimex.com/Products/ARM/ST/STM32-103STK/
 */
class Stm32stk extends Component with HwImplemented with NoIO {
  override val name = "Stm32stk"
  override val description = "STM32-103STK board"
}

/**
 * I/O definitions for the STM32-103STK board.
 *
 * One joystick with 4 directions, a center button an one red LED.
 * The LED is active low, so an inverter is used.
 * An A/D conversion is necessary to read the joystick position.
 */
object Stm32stk {

  /** Analog input */

  // Joystick on `PC.5` (ADC channel 15)
  val adc0_pin = Pin('C', 5)
  lazy val adc0 = AnalogInput(adc0_pin, 15)


  /** Digital input */

  // The center joystick button is on `PC.6`
  val btn0_pin = Pin('C', 6)
  lazy val btn0 = DigitalInput(btn0_pin)


  /** Digital output */

  // The red LED is on `PC.12`
  val led0_pin = Pin('C', 12)
  lazy val led0 = {
    // The board LED is active low. Use and inverter to make it active high.
    val led = DigitalOutput(led0_pin)
    val invert = Not[bool]()
    invert.out --> led.in
    invert // the input is the inverter and not the LED directly
  }
}

/**
 * Custom I/O extension board for the [[Stm32stk]] board.
 *
 * Has 4 buttons, 4 LEDs (2 red, 2 yellow) and a potentiometer.
 * The 2 red LEDs can be used as [[DigitalOutput]] or [[PwmOutput]].
 */
object Stm32stkIO {

  /** Analog input */

  // Potentiometer on `PB.0` (ADC channel 8)
  val adc1_pin = Pin('B', 0)
  lazy val adc1 = AnalogInput(adc1_pin, 8)


  /** Digital inputs */

  // Button 4 on `PC.2` (top)
  val btn4_pin = Pin('C', 2)
  lazy val btn4 = DigitalInput(btn4_pin)

  // Button 3 on `PC.1`
  val btn3_pin = Pin('C', 1)
  lazy val btn3 = DigitalInput(btn3_pin)

  // Button 2 on `PC.0`
  val btn2_pin = Pin('C', 0)
  lazy val btn2 = DigitalInput(btn2_pin)

  // Button 1 on `PB.1` (bottom)
  // val btn1_pin = Pin('B', 1)
  // lazy val btn1 = DigitalInput(btn1_pin)


  /** Digital outputs */

  // Red LED on `PB.9`
  val led4_pin = Pin('B', 9)
  lazy val led4 = DigitalOutput(led4_pin)

  // Red LED on `PB.8`
  val led3_pin = Pin('B', 8)
  lazy val led3 = DigitalOutput(led3_pin)

  // Yellow LED on `PC.4`
  val led2_pin = Pin('C', 4)
  lazy val led2 = DigitalOutput(led2_pin)

  // Yellow LED on `PC.3`
  val led1_pin = Pin('C', 3)
  lazy val led1 = DigitalOutput(led1_pin)


  /** PWM outputs */

  // PWM for led4 on `PB.9`
  val pwm4_pin = led4_pin
  lazy val pwm4 = PwmOutput(pwm4_pin)

  // PWM for led3 on `PB.8`
  val pwm3_pin = led3_pin
  lazy val pwm3 = PwmOutput(pwm3_pin)
}
