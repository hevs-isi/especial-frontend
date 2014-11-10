package hevs.especial.dsl.components

/**
 * Helper object used to do pattern matching on a `Pin` object.
 *
 * A `Pin` can be easily constructed like this: {{{Pin('C', 12)}}}.
 * Identify all input or output of a target.
 */
object Pin {
  def apply(port: Char, pinNumber: Int): Pin = new Pin(port, pinNumber)

  def unapply(p: Pin): Option[(Char, Int)] = Some((p.port, p.pinNumber))
}

/**
 * Define the port and the pin of the GPIO to define it.
 *
 * @constructor create a pin defined by a port letter and a pin number
 * @param port the pin letter (from 'A' to 'F')
 * @param pinNumber the pin number (from 0 to 15)
 */
class Pin(val port: Char, val pinNumber: Int) {
  assert(port >= 'A' && port <= 'F')
  assert(pinNumber >= 0 && pinNumber <= 15)

  override def toString = s"GPIO$port#$pinNumber"
}