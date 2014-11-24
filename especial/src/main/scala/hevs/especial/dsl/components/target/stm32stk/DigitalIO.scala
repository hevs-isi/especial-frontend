package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{Component, Pin}

/**
 * A digital input or output is defined by a unique pin number.
 *
 * @param pin the pin of the GPIO (port and pin number)
 */
abstract class DigitalIO(pin: Pin) extends Component {
  /** Value of the pin structure formatted for the generated code. */
  protected val pinName: String = s"'${pin.port}', ${pin.pinNumber}"
}


