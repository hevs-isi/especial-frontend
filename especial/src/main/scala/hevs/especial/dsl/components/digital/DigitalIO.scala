package hevs.especial.dsl.components.digital

import hevs.especial.dsl.components.fundamentals.{uint1, Component}

/**
 * A digital input or output is defined by a unique pin number.
 *
 * @param pin GPIO pin number
 */
abstract class DigitalIO(val pin: Int) extends Component {
  // A digital input/output can read/write boolean values
  type T = uint1
}
