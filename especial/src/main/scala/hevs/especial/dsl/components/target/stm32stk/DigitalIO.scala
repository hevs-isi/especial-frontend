package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{Component, Pin, uint1}

/**
 * A digital input or output is defined by a unique pin number.
 *
 * @param pin the pin of the GPIO (port and pin number)
 */
abstract class DigitalIO(val pin: Pin) extends Component {
  // A digital input/output can read/write boolean values
  type T = uint1

  /** Unique global variable name to control/access the GPIO */
  protected val valName: String

  /** Print the pin structure as required */
  protected val pinName: String = s"'${pin.port}', ${pin.pinNumber}"
}


