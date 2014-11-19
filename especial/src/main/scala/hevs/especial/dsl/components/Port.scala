package hevs.especial.dsl.components

import hevs.especial.utils.{PortInputShortCircuit, PortTypeMismatch}

import scala.reflect.runtime.universe._

/**
 * A port is an input or output connection of a component.
 *
 * An output port can be connected with an input port using a `Wire`. The type of these two ports must be exactly the
 * same (all `CType` are available).
 * Each port as a unique ID, a name and a description. It can be connected or not. If not, a warning will be generated.
 *
 * @param owner the component owner of the port
 * @tparam T the type of the data transported through the port
 */
abstract class Port[T <: CType : TypeTag](owner: Component) {

  // Name required
  val name: String

  // Optional description
  protected val description: String = ""

  // Unique port ID generated from the component owner
  private val id = owner.nextPortId
  private val tpe: Type = typeOf[T]

  // Count the number of connection with/to this port.
  // - An input port can only have one single connection.
  // - An output port can have many.
  private var connections = 0

  def isNotConnected = !isConnected

  def isConnected = connections > 0 // Has at least one connection

  override def equals(other: Any) = other match {
    // A port ID must be unique. The type of the Port is not checked here.
    case that: Port[_] => that.getId == this.id
    case _ => false
  }

  def getId = id

  override def hashCode = id.##

  override def toString = s"Port[$id] '$name' of Cmp[$getOwnerId] '${getOwner.name}'"

  def getTypeAsString: String = {
    // Something like "hevs.especial.dsl.components.fundamentals.uint1"
    val t: Type = this.getType

    // Return the child class (ex: uint1) as String
    t.baseClasses.head.asClass.name.toString
  }

  /**
   * @return Return the type of the Port.
   */
  protected[components] def getType = tpe

  protected[components] def getDescription = description

  protected[components] def connect() = this match {
    case _: OutputPort[_] =>
      // Connected with at least one other input
      connections += 1
    case _: InputPort[_] =>
      // Cannot connect an input with more than one output
      if (connections > 0)
        throw new PortInputShortCircuit(s"Short circuit: the input '$name' of Cmp[$getOwnerId] '${getOwner.name}'" +
          " is already connected !")
      else {
        connections = 1
      }
  }

  protected[components] def getOwnerId = getOwner.getId

  protected[components] def getOwner = owner

  protected[components] def disconnect() = {
    // FIXME: implementation is missing. Not allowed for now... Must remove the connection in the graph.
    connections = 0
    ???
  }

  /**
   * Helper method to check if two `Port` are of the same type. If not, an `PortTypeMismatch` exception is thrown.
   * @tparam A the type of the port
   * @param that the port to connect with
   * @return true if the types are the same, or an exception is thrown
   */
  protected def checkType[A <: CType : TypeTag](that: Port[A]): Boolean = {
    val tpA = typeOf[A]
    val tpB = typeOf[T]
    if (tpA != tpB)
      throw new PortTypeMismatch(s"Cannot connected '${this.name}' of type '$tpA' to '${that.name}' of type $tpB !")
    true
  }
}

/**
 * An input port.
 * Can be connected with only one `OutputPort`. If not, a `PortInputShortCircuit` will be thrown.
 *
 * @param owner the component owner of the port
 * @tparam T the type of the data transported through the port
 */
abstract class InputPort[T <: CType : TypeTag](owner: Component) extends Port[T](owner) {

  override def toString = "Input" + super.toString

  /**
   * Function used to set the input value of the port.
   * @param s the name of the variable or C code to set as input
   * @return the C code generated to update the input of the port
   */
  protected[components] def setInputValue(s: String): String
}

/**
 * An output port.
 * Can be connected with many `InputPort` using the function `-->`.
 *
 * @param owner the component owner of the port
 * @tparam T the type of the data transported through the port
 */
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

  override def toString = "Output" + super.toString

  /**
   * generate the C code to read the value of this output port.
   * @return the C code to read the output value
   */
  protected[components] def getValue: String
}