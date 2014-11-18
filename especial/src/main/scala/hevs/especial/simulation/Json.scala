package hevs.especial.simulation

import hevs.especial.dsl.components.Pin

object MsgId {
  /**
   * Used to create a command from an `Int`. Match to existing command if possible.
   * @param id the id of the command
   * @return the corresponding id
   */
  def apply(id: Int) = id match {
    case DigitalOut.id => DigitalOut
    case DigitalIn.id => DigitalIn
    case CEvent.id => CEvent
    case Empty.id => Empty
    case _ => new Id(id)
  }

  def unapply(p: MsgId): Option[Int] = {
    // Elegant pattern matching
    Some(p.id)
  }

  private class Id(id: Int) extends MsgId(id)
}

/* Available commands */
sealed abstract class MsgId(val id: Int) {
  override def toString = String.valueOf(id)
}

// According to the C structure `EventId` in file `stm32_p103_emul.h`
private case object DigitalOut extends MsgId(0)
private case object DigitalIn extends MsgId(1)
private case object CEvent extends MsgId(16)
private case object Empty extends MsgId(255)



abstract class JsonMessage(msgId: Int) {
  val id = MsgId(msgId)
}

/**
 * JSON message received from QEMU. Directly extracted to this case class.
 *
 * @param msgId command or peripheral ID
 * @param pin pin with port and number
 * @param value value
 */
case class Command(msgId: Int, pin: Pin, value: Int) extends JsonMessage(msgId) {
  override def toString = s"Cmd[$msgId]: $pin - $value"
}

/**
 * Specialization of a command, without pin.
 *
 * @param msgId event ID
 * @param value value
 */
class Event(val msgId: Int, val value: Int) extends JsonMessage(msgId) {

  override def toString = s"Evt[$msgId]: $value"

  // Useful to compare with predefined events
  override def equals(that: Any) = {
    that.isInstanceOf[Event] &&
      msgId == that.asInstanceOf[Event].msgId &&
      value == that.asInstanceOf[Event].value
  }

  // Used only to create subclasses
  protected def this(id: MsgId, value: Int) = this(id.id, value)
}

/**
 * Contains all available event types.
 */
object Events {

  // According to the C structure `EventId` of the file `qemulogger.h`
  case object MainStart extends Event(CEvent, '0')
  case object EndInit extends Event(CEvent, 'A')
  case object LoopStart extends Event(CEvent, 'B')
  case object LoopTick extends Event(CEvent, 'C')
  case object MainEnd extends Event(CEvent, 'F')
  case object NullEvent extends Event(Empty, '0')
}
