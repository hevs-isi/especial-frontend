package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals.hw_implemented
import hevs.androiduino.dsl.components.fundamentals.Component
import hevs.androiduino.dsl.components.fundamentals.CType
import hevs.androiduino.dsl.components.fundamentals.OutputPort
import hevs.androiduino.dsl.components.fundamentals.InputPort

/*private object MuxID{
	private var id = 0

	def getUniqueID() = {
		id = id + 1
		id
	}
}*/

// FIXME remove outputType
case class Mux2[T<:CType](outputType : T) extends Component("a multiplexer") with hw_implemented {

  // val selVal = uniqueVarName("selValMux")

  override def getOutputs = Some(Seq(out))
  override def getInputs = Some(Seq(in1, in2, sel))

	override def getGlobalConstants = None

	override def getFunctionsDefinitions : Option[String] = {
		var result = s"$outputType mux(){\n"

		result += s"switch"

//		for(wire <- out.wires){
//			result += "\t\t" + wire.b.updateValue(s"buttonValue$pin") + ";\n"
//		}

		result += "\t}\n"
		result += "} // End of function def multiplexer\n"

		Some(result)
	}

	// Anonymous mixin of the trait
	val out = new OutputPort[T](this, Some("Mux output")){
		override def getValue : String = {
			s"mux();"
		}
	}

	// Anonymous mixin of the trait
	val in1 = new InputPort[T](this, Some("Input 1")){
		override def updateValue(s: String): String = {
			// TODO: Here is the code for setting the first input
			s"input1 = $s"
		}
	}

	val in2 = new InputPort[T](this, Some("Input 2")){
		override def updateValue(s: String): String = {
			// TODO: Here is the code for setting the second input
			s"input2 = $s"
		}
	}

	val sel = new InputPort[T](this, Some("Input selector")){
		override def updateValue(s: String): String = {
			// Here is the code to set the selector value
			s"sel = $s"
		}
	}

	/*(this) with Updatable {
		override def updateValue(s: String): String = {
			// TODO: Here is the code for setting the LED !
			s"led$pin = $s"
		}
	}*/
}