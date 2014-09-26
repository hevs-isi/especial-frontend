package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging
import scala.reflect.runtime.universe._

// This class represents an input of a component which can be updated
// from a mixed-in trait

// TODO passer le type du port en template ?
// TODO faire une classe abstraite port ou *trait* ?
// TODO enlever le var, faire ConnectedPort, UnconnectedPort ?

// Description of a port
// Input ot output port of a component. Transport only one type of data.
abstract class Port[T <: CType](owner: Component, desc: Option[String] = None) {
  def getOwner = owner

  // Component owner
  def getOwnerId = owner.id

  // def getType = portType
  def getDescription = desc

  // def getType: String = this.t.getClass.getSimpleName // FIXME how to get the type of a generic class

  def isConnected: Boolean
}

abstract class InputPort[T <: CType](owner: Component, desc: Option[String] = None) extends Port[T](owner, desc) with Logging {

  // FIXME useful or not ? Le wire a 2 ports ou les port ont le wire ?
  var w: Option[Wire] = None

  def setInputWire(in: Wire) = {
    assert(!isConnected, "The input is already connected !")
    w = Some(in)
  }

  def clearInputWire() = {
    w = None
  }

  def isConnected = w.isDefined

  // Abstract function that should created to update the value
  def updateValue(s: String): String

  override def toString = w match {
    case Some(wire) => s"Input connected from ${wire.from}"
    case None => "Input NC"
  }
}

// FIXME +T with CType ??
abstract class OutputPort[T <: CType](owner: Component, desc: Option[String] = None) extends Port[T](owner, desc) {

  // FIXME list of wires here ?
  var wires: List[Wire] = List.empty

  /**
   * Connect and `OutputPort` to an `InputPort`.
   * @param that
   * @return
   */
  def -->(that: InputPort[_]) = {
    // Add a wire to the output port
    val w = new Wire(this, that) // Create a wire from this (output) to that (input)
    that.setInputWire(w)
    wires ::= w

    // Add the directed edge in the graph
    ComponentManager.addWire(w)
  }

  def updateConnected() = {
    for (wire <- wires) {
      wire.to.updateValue(wire.from.getValue)
    }
  }

  // Abstract function
  def getValue: String

  def isConnected = !wires.isEmpty

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