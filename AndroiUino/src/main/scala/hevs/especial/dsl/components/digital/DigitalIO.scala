package hevs.androiduino.dsl.components.digital

import hevs.androiduino.dsl.components.fundamentals.{Component, uint1}

/**
 * A digital input or output is defined by a unique pin number.
 *
 * @param pin
 */
abstract class DigitalIO(val pin: Int) extends Component {
  // A digital input/output can read/write boolean values
  type T = uint1
}
