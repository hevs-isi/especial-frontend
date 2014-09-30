package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.utils.WireConnection

import scala.reflect.runtime.universe._

// This class represents an input of a component which can be updated
// from a mixed-in trait

// TODO passer le type du port en template ?
// TODO faire une classe abstraite port ou *trait* ?
// TODO enlever le var, faire ConnectedPort, UnconnectedPort ?

// Description of a port
// Input ot output port of a component. Transport only one type of data.
abstract class Port[T <: CType : TypeTag](owner: Component) {

  // Port description (optional)
  val description: String = ""

  def getOwner = owner

  // Component owner
  def getOwnerId = owner.id

  def isConnected: Boolean
  def isNotConnected = !isConnected

  // Helper method to check if two `Port` contains the same type of data.
  def isSameTypeAs[A <: CType : TypeTag](that: Port[A]): Boolean = {
    // TODO check and remove debug print
    // println("this is of type: " + typeOf[T])
    // println("that is of type: " + typeOf[A])
    typeOf[T] == typeOf[A]
  }
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

  private var connected = false

  def connect() = {
    assert(isNotConnected, "Input already connected !")
    connected = true
  }
  def disconnect() = connected = false


  def isConnected = connected

  // Abstract function that should created to update the value
  def updateValue(s: String): String

  override def toString = isConnected match {
    case true => s"Input connected"
    case _ => "Input NC"
  }
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

    // Check the type of the connection
    val sameType = isSameTypeAs(that)
    sameType match {
      case false => throw new WireConnection("Connection types error !")
      case _ =>
    }

    that.connect()

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

  def isConnected = false //!wires.isEmpty // FIXME

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