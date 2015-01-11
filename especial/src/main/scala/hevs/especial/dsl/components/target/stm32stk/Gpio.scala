package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{Component, Pin}

/**
 * A General Purpose Input/Output is defined by a unique [[Pin]].
 *
 * @param pin the pin of the GPIO (port and pin number)
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
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

  override def toString = {
    // Print the port and pin number of the GPIO
    val pinName = "%s#%02d" format(pin.port, pin.pinNumber)
    s"Cmp[$getId] '$name' ($pinName)"
  }
}


