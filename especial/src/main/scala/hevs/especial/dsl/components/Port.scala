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
 *
 * @version 1.1
 * @author Christopher Metrailler (mei@hevs.ch)
 */
abstract class Port[+T <: CType : TypeTag](owner: Component) {

  /** The port name is required to identify it. */
  val name: String

  /** Optional port description. */
  protected val description: String = ""

  // Unique port ID generated from the component owner
  private val id = owner.nextPortId
  private val tpe = typeOf[T]

  // Count the number of connection with/to this port.
  // - An input port can only have one single connection.
  // - An output port can have many.
  protected var connections = 0

  /** @see isConnected */
  def isNotConnected = !isConnected

  /**
   * A port is connected when it has at least one connection with another `Port`.
   * An `OutputPort`can have 1 or more connections, an `InputPort` one only.
   *
   * @return `true` if the port is connected, `false` otherwise.
   */
  def isConnected = connections > 0

  /**
   * Check if two ports are the same (equals). Ports are identified by a unique ID.
   * @param other other object to equals
   * @return `true` if the port as the same ID
   */
  override def equals(other: Any) = other match {
    // A port ID must be unique. The type of the Port is not checked here.
    case that: Port[_] => that.getId == this.id
    case _ => false
  }

  /**
   * Get the unique ID of the [[Port]].
   * Used by the graph library to make edges between ports.
   * @return the unique id of the port
   */
  def getId: Int = id

  override def hashCode = id.## // Use by the graph library

  override def toString = {
    val sConn = if (isConnected) "" else " (NC)"
    val cmpId = f"$getOwnerId%02d"
    s"Port[$id] '$name' of Cmp[$cmpId] '${getOwner.name}'$sConn"
  }

  /**
   * Return the value type of the port as a `String`.
   * @see getType
   * @return the value type of the port (ex: `bool`)
   */
  def getTypeAsString: String = {
    // Something like "hevs.especial.dsl.components.fundamentals.bool"
    val t: Type = this.getType
    t.baseClasses.head.asClass.name.toString // Return the child class (ex: bool) as String
  }

  /**
   * Return the value type of the port as a `Type`.
   *
   * @see getTypeAsString
   * @return the value type of the port
   */
  protected[components] def getType: Type = tpe

  /**
   * @return optional description of the port. Empty by default.
   */
  protected[components] def getDescription = description

  /**
   * Set this port as connected.
   */
  protected[components] def connect(): Unit

  /**
   * Reset the number of connections for this port.
   * Used for test purpose only.
   */
  def disconnect(): Unit = {
    // Just reset the number of connection.
    // The port is now considered as unconnected, but the graph will NOT be modified in this method.
    connections = 0
  }

  protected[components] def getOwnerId = getOwner.getId

  def getOwner = owner

  /**
   * Helper method to check if two `Port` are of the same type. If not, an `PortTypeMismatch` exception is thrown.
   * @param that the port to connect with
   * @tparam A the type of the input port to connect with
   * @return true if the types are the same, or an exception is thrown
   */
  @throws(classOf[PortTypeMismatch])
  protected def checkType[A <: CType : TypeTag](that: InputPort[A]): Boolean = {
    val tpB = typeOf[A]
    // The output type must be the same type as the input type
    if (tpe != tpB)
      throw PortTypeMismatch(this, that)
    else
      true // Type are correct. Connection is valid !
  }
}

/**
 * An input port.
 * Can be connected with only one `OutputPort`. If not, a `PortInputShortCircuit` will be thrown.
 *
 * @param owner the component owner of the port
 * @tparam T the type of the data transported through the port
 */
abstract class InputPort[+T <: CType : TypeTag](owner: Component) extends Port[T](owner) {

  override def toString = "Input" + super.toString

  /**
   * Set this port as connected.
   * An input can be connected once only. An exception is thrown if already connected.
   *
   * @throws hevs.especial.utils.PortInputShortCircuit in the input is already connected
   */
  @throws(classOf[PortInputShortCircuit])
  final override def connect(): Unit = {
    // Cannot connect an input with more than one output
    if (connections > 0)
      throw PortInputShortCircuit(this)
    else
      connections = 1
  }
}

/**
 * An output port.
 * Can be connected with many `InputPort` using the function `-->`.
 *
 * @param owner the component owner of the port
 * @tparam T the type of the data transported through the port
 */
abstract class OutputPort[+T <: CType : TypeTag](owner: Component) extends Port[T](owner) {

  override def toString = "Output" + super.toString

  /**
   * Improvement for constant propagation. See issue #13.
   * Indicates if the port output is a constant value or not (`false` by default).
   * If all inputs are constant values, the output is automatically converted to a constant value.
   */
  // val isConstant: Boolean = false

  /**
   * Set this port as connected.
   * An output can be connected many times.
   */
  final override def connect(): Unit = {
    // Connect this output port with one more input
    connections += 1
  }

  /**
   * Connect and `OutputPort` to an `InputPort`.
   * The `InputPort` must be unconnected or an exception is thrown. If port types are not the same,
   * a `PortTypeMismatch` exception will be thrown.
   *
   * @param that the input to connect with this output
   * @tparam A the type of the `InputPort` to connect with
   * @throws hevs.especial.utils.PortTypeMismatch ports types mismatch
   * @throws hevs.especial.utils.PortInputShortCircuit `InputPort` already connected
   * @return `true` if the connection is valid, otherwise an exception is thrown.
   */
  @throws(classOf[PortTypeMismatch])
  @throws(classOf[PortInputShortCircuit])
  def -->[A <: CType : TypeTag](that: InputPort[A]): Boolean = {
    // Connection types check. `PortTypeMismatch` is thrown if not valid.
    checkType(that)

    that.connect() // Thrown an exception if not valid
    this.connect()
    ComponentManager.addWire(this, that) // Add the directed edge in the graph

    true // Valid if no exception have been thrown
  }

  /**
   * Read the value of an [[OutputPort]].
   *
   * Usually, the output value of a component (for a specific port) is computed in the while loop. The result of the
   * computation is stored in a local variable. To read the port value, the name of the local variable must be
   * simply returned as a String.
   *
   * @return the C code to read the output value or the value itself (a variable name or a constant value)
   */
  def getValue: String
}


