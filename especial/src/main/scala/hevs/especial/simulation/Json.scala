package hevs.especial.simulation

import hevs.especial.dsl.components.Pin

object PeriphId {
  /**
   * Used to create a command from an `Int`. Match to existing command if possible.
   * @param id the id of the command
   * @return the corresponding id
   */
  def apply(id: Int) = id match {
    case DigitalOut.id => DigitalOut
    case DigitalIn.id => DigitalIn
    case _ => new Id(id)
  }

  def unapply(p: PeriphId): Option[Int] = {
    // Elegant pattern matching
    Some(p.id)
  }

  private class Id(id: Int) extends PeriphId(id)

}

/* Available commands */
sealed abstract class PeriphId(val id: Int)

case object DigitalOut extends PeriphId(0)
case object DigitalIn extends PeriphId(1)


/**
 * JSON message received from QEMU. Directly extracted to this case class.
 *
 * @param periph peripheral ID
 * @param pin pin with port and number
 * @param value value
 */
case class Command(private val periph: Int, pin: Pin, value: Int) {

  val id: PeriphId = PeriphId(periph)

  def this(id: PeriphId, pin: Pin, value: Int) {
    this(id.id, pin, value)
  }
}

