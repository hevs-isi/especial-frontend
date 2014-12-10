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

  /* Code generation */
  // FIXME: not used any more...
  // override def getIncludeCode = Seq("helper.h")
}

/**
 * I/O definitions.
 */
object Stm32stk {

  /** The red LED is on `PC.12`. */
  val led0_pin = Pin('C', 12)

  // The board LED is active low. Use and inverter to make it active high.
  lazy val led0 = {
    val led = DigitalOutput(led0_pin)
    val invert = Not[bool]()
    invert.out --> led.in
    invert // the input is the inverter and not the LED directly
  }

  /** The center joystick button is on `PC.6`. */
  val btn0_pin = Pin('C', 6)
  lazy val btn0 = DigitalInput(btn0_pin)
}

/**
 * I/O extension board for the [[Stm32stk]] kit.
 */
object Stm32stkIO {

  // val adc1_pin = Pin('B', 0)

  // val btn1_pin = Pin('B', 1)
  // lazy val btn1 = DigitalInput(btn1_pin)

  /** Button 2 on `PC.0`. */
  val btn2_pin = Pin('C', 0)
  lazy val btn2 = DigitalInput(btn2_pin)

  /** Button 3 on `PC.1`. */
  val btn3_pin = Pin('C', 1)
  lazy val btn3 = DigitalInput(btn3_pin)

  /** Button 4 on `PC.2`. */
  val btn4_pin = Pin('C', 2)
  lazy val btn4 = DigitalInput(btn4_pin)



  /** Yellow LED on `PC.3`. */
  val led1_pin = Pin('C', 3)
  lazy val led1 = DigitalOutput(led1_pin)

  /** Yellow LED on `PC.4`. */
  val led2_pin = Pin('C', 4)
  lazy val led2 = DigitalOutput(led2_pin)

  /** Red LED on `PB.8`. */
  val led3_pin = Pin('B', 8)
  lazy val led3 = DigitalOutput(led3_pin)

  /** Red LED on `PB.9`. */
  val led4_pin = Pin('B', 9)
  lazy val led4 = DigitalOutput(led4_pin)
}
