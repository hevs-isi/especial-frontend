package hevs.especial.dsl.components

import grizzled.slf4j.Logging
import hevs.especial.utils.{PortInputShortCircuit, PortTypeMismatch}

import scala.reflect.runtime.universe._

// TODO: documentation + cleanup

// This class represents an input of a component which can be updated
// from a mixed-in trait

// Description of a port
// Input ot output port of a component. Transport only one type of data.
abstract class Port[T <: CType : TypeTag](owner: Component) {

  val typeMirror = runtimeMirror(this.getClass.getClassLoader)
  val instanceMirror = typeMirror.reflect(this)

  // Optional description
  protected val description: String = ""

  private val id = owner.newUniquePortId
  private val tpe: Type = typeOf[T]
  private var connected = false
  private var connections = 0

  def getDescription = description

  def getOwnerId = getOwner.getId

  def connect() = this match {
    case _: OutputPort[_] =>
      // Connected with at least one other input
      connections += 1
      connected = true
    case _: InputPort[_] =>
      // Cannot connect an input with more than one output
      if (connections > 0)
        throw new PortInputShortCircuit("Short circuit: the input is already connected !")
      else {
        connected = true
        connections = 1
      }
  }

  def isNotConnected = !isConnected

  def isConnected = connected

  def disconnect() = connected = false

  override def equals(other: Any) = other match {
    // A port ID must be unique. The type of the Port is not checked here.
    case that: Port[_] => that.getId == this.id
    case _ => false
  }

  def getId = id

  override def hashCode = id.##

  /**
   * Helper method to check if two `Port` are of the same type. If not, an `PortTypeMismatch` exception is thrown.
   * @tparam A the type of the port
   * @param that the port to connect with
   * @return true if the types are the same, or an exception is thrown
   */
  def checkType[A <: CType : TypeTag](that: Port[A]): Boolean = {
    val tpA = typeOf[A]
    val tpB = typeOf[T]
    if (tpA != tpB)
      throw new PortTypeMismatch(s"Cannot connected $tpA to $tpB !")
    true
  }

  /**
   * @return Return the type of the Port.
   */
  def getType = tpe

  import scala.reflect.runtime.universe._

  def getTypeAsString: String = {
    // Something like "hevs.especial.dsl.components.fundamentals.uint1"
    val t: Type = this.getType

    // Return the child class (ex: uint1) as String
    t.baseClasses.head.asClass.name.toString
  }

  def getOwner = owner

  override def toString = s"Port[$id] of $getOwner"
}

abstract class InputPort[T <: CType : TypeTag](owner: Component) extends Port[T](owner) with Logging {

  // TODO: documentation
  // C code to set the value of an input port
  def setInputValue(s: String): String

  override def toString = "Input" + super.toString
}

abstract class OutputPort[T <: CType : TypeTag](owner: Component) extends Port[T](owner) {

  /**
   * Connect and `OutputPort` to an `InputPort`. The `InputPort` must be unconnected or an exception is thrown.
   * @param that the input to connect with this output
   * @return
   */
  def -->(that: InputPort[T]) = {
    // Check the type of the connection. An error is thrown if the connection is not valid.
    checkType(that)

    that.connect()
    this.connect()

    ComponentManager.addWire(this, that) // Add the directed edge in the graph
  }

  // TODO: documentation
  def getValue: String

  override def toString = "Output" + super.toString

}