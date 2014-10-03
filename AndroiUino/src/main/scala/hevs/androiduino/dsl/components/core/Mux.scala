package hevs.androiduino.dsl.components.core

import hevs.androiduino.dsl.components.fundamentals.{CType, Component, InputPort, OutputPort, hw_implemented}

import scala.reflect.runtime.universe._

// FIXME remove outputType and use template (how ?)

@Deprecated
case class Mux2[T <: CType : TypeTag](outputType: T) extends Component with hw_implemented {

  override val description = "multiplexer with wto inputs"

  // val selVal = uniqueVarName("selValMux")
  val out = new OutputPort[T](this) {

    override val description = "mux output"

    override def getValue: String = {
      s"mux();"
    }
  }
  val in1 = new InputPort[T](this) {

    override val description = "input 1"

    override def setInputValue(s: String): String = {
      // TODO: Here is the code for setting the first input
      s"input1 = $s"
    }
  }
  val in2 = new InputPort[T](this) {


    override def setInputValue(s: String): String = {
      // TODO: Here is the code for setting the second input
      s"input2 = $s"
    }
  }
  val sel = new InputPort[T](this) {

    override val description = "input selector"

    override def setInputValue(s: String): String = {
      // Here is the code to set the selector value
      s"sel = $s"
    }
  }

  def getOutputs = Some(Seq(out))

  def getInputs = Some(Seq(in1, in2, sel))

  override def getGlobalCode = None

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