package hevs.especial.dsl.components

object Pin {
  // Use the second constructor
  def apply(port: String, pinNumber: Int) = {
    assert(port.length > 0)
    new Pin(port, pinNumber)
  }
}

/**
 * Define the port and the pin of the GPIO to define it.
 *
 * A `Pin` can be easily constructed like this: {{{Pin('C', 12)}}}.
 * Identify all input or output of a target.
 *
 * @constructor create a pin defined by a port letter and a pin number
 * @param port the port letter (from 'A' to 'F')
 * @param pinNumber the pin number (from 0 to 15)
 */
case class Pin(port: Char, pinNumber: Int) {

  def this(port: String, nbr: Int) {
    // Take the first char of the name. Constructor used by the JSON extractor
    // because the port name is a String in the JSON message (Char type not available).
    this(port(0), nbr)
  }

  require(port >= 'A' && port <= 'G', "Invalid port name")
  require(pinNumber >= 0 && pinNumber <= 15, "Invalid pin number")

  // Pin identifier as a String value. Used as identifier in the VCD file. Cannot contains any special character.
  def getIdentifier = s"$port$pinNumber"

  override def toString = s"Pin $port#$pinNumber"
}