package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals.hw_implemented
import hevs.androiduino.dsl.components.fundamentals.CType
import hevs.androiduino.dsl.components.fundamentals.Component
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.fundamentals.InputPort
import hevs.androiduino.dsl.components.fundamentals.OutputPort

case class BitExtractor[T<:CType](inputType : T) extends Component("a bit extractor") with hw_implemented {
	override def getOutputs() = out :: Nil
	override def getInputs() = in1 :: Nil
	
	val in1 = new InputPort(inputType, this, Some("All bits input")){
		override def updateValue(s: String): String = {
			// TODO: Here is the code for setting the first input
			s"input1 = $s" // TODO: tbd
		}
	}
	
	val out = new OutputPort(uint1(), this, Some("Extracted bit")){		
		override def getValue() : String = {
			//"Here is the code for getting the output of the extractor
		   s"// extracted bit code" // TODO
		}	
	}
}