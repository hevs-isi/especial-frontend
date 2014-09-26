package hevs.androiduino.dsl.components.fundamentals

// This class represents an input of a component which can be updated
// from a mixed-in trait

// TODO passer le type du port en template ?
// TODO faire une classe abstraite port ou *trait* ?
// TODO enlever le var, faire ConnectedPort, UnconnectedPort ?

// Description of a port
// Input ot output port of a component. Transport only one type of data.
abstract class Port[T <: CType](owner: Component, desc: Option[String] = None) {
  def getOwner = owner // Component owner
  def getOwnerId = owner.id

  // def getType = portType
  def getDescription = desc

  def isConnected: Boolean
}

abstract class InputPort[T <: CType](owner: Component, desc: Option[String] = None) extends Port[T](owner, desc) {

  // FIXME useful or not ? Le wire a 2 ports ou les port ont le wire ?
  var w: Option[Wire] = None

  def setInputWire(in: Wire) = {
    w = Some(in)
  }

  def clearInputWire() = {
    w = None
  }

  def isConnected = w.isDefined

  // Abstract function that should created to update the value
  def updateValue(s: String): String

  override def toString = w match {
    case Some(wire) => s"coming from component ${wire.a.getOwnerId}"
    case None => "NC"
  }
}

abstract class OutputPort[T <: CType](owner: Component, desc: Option[String] = None) extends Port[T](owner, desc) {

  // FIXME list of wires here ?
  var wires: List[Wire] = List.empty

  def -->(other: InputPort[_]) = {
    // Add a wire to the output port
    val w = new Wire(this, other)
    other.setInputWire(w)
    wires ::= w

    // Add the directed edge in the graph
    ComponentManager.addWire(owner, other.getOwner)
  }

  def updateConnected() = {
    for (wire <- wires) {
      wire.b.updateValue(wire.a.getValue)
    }
  }

  // Abstract function
  def getValue: String

  def isConnected = !wires.isEmpty

  override def toString = {
    var result = ""
    for (wire <- wires) {
      result += "going to [ID" + wire.b.getOwnerId + "]"
    }
    result
  }
}