package hevs.androiduino.dsl.components.digital

import hevs.androiduino.dsl.components.fundamentals.{OutputPort, hw_implemented, uint1}

case class DigitalInput(override val pin: Int) extends DigitalIO(pin) with hw_implemented {

  override val description = s"Digital input on pin $pin"

  /**
   * The `uint1` value of this digital input.
   */
  val out = new OutputPort[T](this) {

    override val description = "Digital input value"

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