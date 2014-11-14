package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{Pin, Component, NoIO, hw_implemented}

/**
 * STM32-103STK board
 * https://www.olimex.com/Products/ARM/ST/STM32-103STK/
 *
 * Some pins are defines for led and buttons.
 */
class Stm32stk extends Component with hw_implemented with NoIO {

  override val description = "STM32-103STK board"

  /* Code generation */
  override def getIncludeCode = Seq("helper.h")
}

/**
 * Useful I/O definitions of some components available on the board.
 */
object Stm32stk {

  /** The red LED is on `GPIOC.12` */
  val pin_led = Pin('C', 0xC)

  /** The center joystick button is on `GPIOC.6` */
  val pin_btn = Pin('C', 0x6)
}

