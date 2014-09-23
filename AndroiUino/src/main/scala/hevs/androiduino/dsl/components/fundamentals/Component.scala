package hevs.androiduino.dsl.components.fundamentals

abstract class Component(val description: String) {
	val id = ComponentManager.addComponent(this)

	def getOutputs(): List[OutputPort]
	def getInputs(): List[InputPort]

	override def toString = {
		"[ID" + id + "] " + description
	}

	def getFullDescriptor = {
		toString +
			"\n\twith inputs : " + getInputs().mkString(", ") +
			"\n\twith outputs : " + getOutputs().mkString(", ")
	}
}

trait hw_implemented {
	// Code inserted to init the component (executed 
	// once in C++) 
	def getInitCode(): Option[String] = None

	def getBeginOfMainAfterInit(): Option[String] = None

	// Code inserted in the main loop of the C++
	def getLoopableCode(): Option[String] = None

	// Code inserted in the global section of the C++
	def getGlobalConstants(): Option[String] = None

	// Code inserted in for function definitions (pre-main)
	def getFunctionsDefinitions(): Option[String] = None
	
}

//abstract class Button(description: String) extends Component(description)
//class SW_Button(pin: Int) extends Button("A software button")
//class HW_Button(pin: Int) extends Button("An hardware button")

