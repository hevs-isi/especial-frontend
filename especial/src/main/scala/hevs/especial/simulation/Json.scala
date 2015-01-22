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

/* Events types according to the C structure `EventId` in file `stm32_p103_emul.h` */

/** From QEMU to indicates when a digital output value has changed. */
private case object DigitalOut extends MsgId(0)

/** From QEMU to indicate when an event has been reached in the code. */
private case object CEvent extends MsgId(16)

/** To QEMU to set an input value. */
private case object DigitalIn extends MsgId(1)

/** Null or empty event (unknown ID). */
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
 * A Json message is extracted to this class directly.
 *
 * @param msgId the event ID
 * @param value the event value
 */
class Event(val msgId: Int, value: Int) extends JsonMessage(msgId) {

  override def toString = s"Evt[$msgId]: $value"

  // Useful to compare with predefined events
  override def equals(that: Any) = {
    that.isInstanceOf[Event] &&
      msgId == that.asInstanceOf[Event].msgId &&
      value == that.asInstanceOf[Event].getValue
  }

  // Used only to create subclasses
  protected def this(id: MsgId, value: Int) = this(id.id, value)

  def getValue: Int = value
}

/**
 * Event sent to QEMU. Raw event value is a byte buffer.
 * @param msgId the event ID
 * @param byteValue the raw event value (typically 4 bytes)
 */
class ByteEvent(override val msgId: Int, val byteValue: Array[Byte]) extends Event(msgId, 0) {
  override def toString = s"ByteEvent[$msgId]: ${byteValue.mkString(", ")}"
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
