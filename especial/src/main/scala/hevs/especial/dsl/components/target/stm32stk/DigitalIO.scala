package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{Component, Pin, bool}

/**
 * A digital input or output is defined by a unique pin number.
 *
 * @param pin the pin of the GPIO (port and pin number)
 */
abstract class DigitalIO(val pin: Pin) extends Component {
  /** A digital input/output can read/write boolean values. */
  protected type T = bool

  /** Value of the pin structure formatted for the generated code. */
  protected val pinName: String = s"'${pin.port}', ${pin.pinNumber}"
}


