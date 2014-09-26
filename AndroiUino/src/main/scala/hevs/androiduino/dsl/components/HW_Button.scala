package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals.hw_implemented
import hevs.androiduino.dsl.components.fundamentals.Component
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.fundamentals.OutputPort

case class HW_Button(pin: Int) extends Component("an hardware button on pin " + pin) with hw_implemented {

  // Anonymous mixin of the trait
  val out = new OutputPort[uint1](this) {
    override def getValue: String = {
      //"Here is the code for getting the output of a button! // TODO\n" +
      s"pollButton$pin();"
    }
  }

  def getOutputs = Some(Seq(out))

  def getInputs = None


  override def getLoopableCode: Option[String] = {
    Some(s"pollButton$pin();")
  }

  override def getFunctionsDefinitions: Option[String] = {
    var result = s"void pollButton$pin(){\n"

    result += "\tif(valueHasChanged){\n"

    for (wire ← out.wires) {
      result += "\t\t" + wire.to.updateValue(s"buttonValue$pin") + ";\n"
    }

    result += "\t}\n"
    result += "} // End of function def for HW_Button\n"

    Some(result)
  }

  override def getInitCode = {
    Some(s"\t// This is the init code for the init of a button\n\tbutton$pin.setAsOutput; // TODO replace this with real code")
  }
}