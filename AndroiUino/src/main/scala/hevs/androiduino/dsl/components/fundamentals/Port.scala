package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.utils.{PortInputShortCircuit, PortTypeMismatch}

import scala.reflect.runtime.universe._

// This class represents an input of a component which can be updated
// from a mixed-in trait

// TODO passer le type du port en template ?
// TODO faire une classe abstraite port ou *trait* ?
// TODO enlever le var, faire ConnectedPort, UnconnectedPort ?

// Description of a port
// Input ot output port of a component. Transport only one type of data.
abstract class Port[T <: CType : TypeTag](owner: Component) {

  // Optional description
  protected val description: String = ""
  private val id = owner.newUniquePortId
  protected var connected = false

  def getDescription = description

  def getOwnerId = getOwner.getId

  def getOwner = owner

  def connect() = this match {
    case _: OutputPort[_] => connected = true
    case _: InputPort[_] =>
      // Cannot connect an input twice
      if (isConnected)
        throw new PortInputShortCircuit("Short circuit: the input is already connected !")
      else
        connected = true
  }

  def isConnected = connected

  def isNotConnected = !isConnected

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
   * @param that the port to connect with
   * @tparam A The type of the port
   * @return true if the types are the same, or an exception is thrown
   */
  def checkType[A <: CType : TypeTag](that: Port[A]): Boolean = {
    // TODO check and remove debug print
    // println("this is of type: " + typeOf[T])
    // println("that is of type: " + typeOf[A])
    val tpA = typeOf[A]
    val tpB = typeOf[T]
    if (tpA != tpB)
      throw new PortTypeMismatch(s"Cannot connected $tpA to $tpB !")
    true
  }

  override def toString = s"Port[$id] of $getOwner"
}

abstract class InputPort[T <: CType : TypeTag](owner: Component) extends Port[T](owner) with Logging {

  // FIXME useful or not ? Le wire a 2 ports ou les port ont le wire ?
  //var w: Option[Wire] = None

  /*def setInputWire(in: Wire) = {
    assert(!isConnected, "The input is already connected !")
    w = Some(in)
  }

  def clearInputWire() = {
    w = None
  }*/

  /*override def connect() = {
    assert(isNotConnected, "Input already connected !")
    connected = true
  }*/

  // C code to set the value of an input port
  def setInputValue(s: String): String

  override def toString = "Input" + super.toString
}

abstract class OutputPort[T <: CType : TypeTag](owner: Component) extends Port[T](owner) {

  // FIXME list of wires here ?
  // var wires: List[Wire] = List.empty

  /**
   * Connect and `OutputPort` to an `InputPort`. The `InputPort` must be unconnected or an exception is thrown.
   * @param that
   * @return
   */
  def -->(that: InputPort[T]) = {
    // Check the type of the connection. An error is thrown if the connection is not valid.
    checkType(that)

    that.connect()
    this.connect()

    // Add the directed edge in the graph
    ComponentManager.addWire(this, that)
  }

  /*def updateConnected() = {
    for (wire <- wires) {
      wire.to.updateValue(wire.from.getValue)
    }
  }*/

  // Abstract function
  def getValue: String

  override def toString = "Output" + super.toString

  /*override def toString = isConnected match {
    case true => {
      // FIXME not beautiful
      var result = ""
      for (wire <- wires) {
        result += "going to [ID" + wire.to.getOwnerId + "]"
      }
      result
    }
    case false => "Output NC"
  }*/
}