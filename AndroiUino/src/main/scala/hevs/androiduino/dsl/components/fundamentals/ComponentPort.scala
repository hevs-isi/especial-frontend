package hevs.androiduino.dsl.components.fundamentals
import scalax.collection.GraphPredef._

// This class represents an input of a component which can be updated
// from a mixed-in trait
abstract class InputPort(val t : CType, val owner: Component, val desc : Option[String] = None) {
	var w: Wire = null

	def setInputWire(in: Wire) = {
		w = in
	}
	
	def isConnected = w != null
	
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
	var wires: List[Wire] = List.empty[Wire]
	
	def -->(other: InputPort) = {			
		// Add a wire to the output port
		val w = new Wire(this, other)
		other.setInputWire(w)
		wires ::= w
		ComponentManager.gr1 += (owner~>other.owner)
	}

	def updateConnected() = {
		for(wire <- wires){
			wire.b.updateValue(wire.a.getValue)
		}
	}
	
	def getValue : String
	
	def isConnected = wires != null
	
	override def toString = {
		var result = ""
		for(wire <- wires){
			result += "going to [ID" +  wire.b.owner.id + "]"
		} 
		result
	}
}