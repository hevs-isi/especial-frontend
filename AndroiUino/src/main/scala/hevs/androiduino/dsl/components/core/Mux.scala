package hevs.androiduino.dsl.components.core

import hevs.androiduino.dsl.components.fundamentals.{CType, Component, InputPort, OutputPort, hw_implemented}

import scala.reflect.runtime.universe._

/*private object MuxID{
	private var id = 0

	def getUniqueID() = {
		id = id + 1
		id
	}
}*/

// FIXME remove outputType and use template (how ?)

case class Mux2[T <: CType : TypeTag](outputType: T) extends Component with hw_implemented {

  override val description = "a multiplexer with wto inputs"

  // val selVal = uniqueVarName("selValMux")
  val out = new OutputPort[T](this) {

    override val description = "Mux output"

    override def getValue: String = {
      s"mux();"
    }
  }
  val in1 = new InputPort[T](this) {

    override val description = "Input 1"

    override def readValue(s: String): String = {
      // TODO: Here is the code for setting the first input
      s"input1 = $s"
    }
  }
  val in2 = new InputPort[T](this) {


    override def readValue(s: String): String = {
      // TODO: Here is the code for setting the second input
      s"input2 = $s"
    }
  }
  val sel = new InputPort[T](this) {

    override val description = "Input selector"

    override def readValue(s: String): String = {
      // Here is the code to set the selector value
      s"sel = $s"
    }
  }

  def getOutputs = Some(Seq(out))

  def getInputs = Some(Seq(in1, in2, sel))

  override def getGlobalConstants = None

  override def getFunctionsDefinitions: Option[String] = {
    var result = s"$outputType mux(){\n"

    result += s"switch"

    //		for(wire <- out.wires){
    //			result += "\t\t" + wire.b.updateValue(s"buttonValue$pin") + ";\n"
    //		}

    result += "\t}\n"
    result += "} // End of function def multiplexer\n"

    Some(result)
  }

  /*(this) with Updatable {
    override def updateValue(s: String): String = {
      // TODO: Here is the code for setting the LED !
      s"led$pin = $s"
    }
  }*/
}