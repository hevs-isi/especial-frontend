package hevs.androiduino.dsl.components.fundamentals

// This class represents an input of a component which can be updated
// from a mixed-in trait

// TODO passer le type du port en template ?
// TODO faire une classe abstraite port ou *trait* ?
// TODO enlever le var, faire ConnectedPort, UnconnectedPort ?

abstract class InputPort(val t : CType, val owner: Component, val desc : Option[String] = None) {

  // FIXME useful or not ? Le wire a 2 ports ou les port ont le wire ?
  var w: Wire = null

	def setInputWire(in: Wire) = {
		w = in
	}
	
	def isConnected = w != null // TODO move to top abstract class or trait
	
	// Abstract function that should created to update the value
	def updateValue(s : String) : String
	
	override def toString = {
		if(isConnected)
			"coming from component with [ID" + w.a.owner.id + "]"
		else
			"NC"
	}
}

abstract class OutputPort(val t : CType, val owner: Component, val desc: Option[String] = None) {

  // FIXME list of wires here ?
  var wires: List[Wire] = List.empty
	
	def -->(other: InputPort) = {			
		// Add a wire to the output port
		val w = new Wire(this, other)
		other.setInputWire(w)
		wires ::= w

    // Add the directed edge in the graph
    ComponentManager.addWire(owner, other.owner)
	}

	def updateConnected() = {
		for(wire <- wires){
			wire.b.updateValue(wire.a.getValue)
		}
	}

  // Abstract function
	def getValue : String
	
	def isConnected = wires.size > 0 // TODO move to top abstract class or trait
	
	override def toString = {
		var result = ""
		for(wire <- wires){
			result += "going to [ID" +  wire.b.owner.id + "]"
		} 
		result
	}
}