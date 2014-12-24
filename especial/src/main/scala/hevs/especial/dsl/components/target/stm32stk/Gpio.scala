package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{Component, Pin}

/**
 * A GPIO (input or output) is defined by a unique pin number.
 * This component is unique in the graph and identified by is pin and port.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @param pin the pin of the GPIO (port and pin number)
 */
abstract class Gpio(private val pin: Pin) extends Component {
  
  /** Value of the pin structure formatted for the generated code. */
  protected val pinName: String = s"'${pin.port}', ${pin.pinNumber}"


  /* Unique component */

  override def equals(other: Any) = other match {
    // The pin number must be unique for each GPIO
    case that: Gpio => that.pin == this.pin
    case _ => false
  }

  override def hashCode = pin.##
}


