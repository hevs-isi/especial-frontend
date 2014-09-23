package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals.hw_implemented
import hevs.androiduino.dsl.components.fundamentals.Component
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.fundamentals.InputPort

abstract class Led(description: String) extends Component(description) {
	//	protected[this] var _status: uint1 = uint1(false)
}

case class HW_Led(pin: Int) extends Led("an hardware led on pin " + pin) with hw_implemented {

	// Anonymous mixin of the trait
	val in = new InputPort(uint1(), this){
		override def updateValue(s: String): String = {
			// TODO: Here is the code for setting the LED ! 
			s"led$pin = $s"
		}
	}
	
	override def getOutputs() = Nil
	override def getInputs() = in :: Nil

	override def getInitCode() = {
		Some(s"\t// This is the init code for the init of an led\n\tint reg_led$pin = init_value; // TODO replace this with real code")
	}
}