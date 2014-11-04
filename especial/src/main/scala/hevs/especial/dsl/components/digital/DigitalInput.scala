package hevs.especial.dsl.components.digital

import hevs.especial.dsl.components._

case class DigitalInput(override val pin: Int) extends DigitalIO(pin) with Out1 with hw_implemented {

  override val description = s"digital input on pin $pin"
  private val valName = s"digitalIn$getVarId" // unique variable name
  private val fctName = s"pollDigitalInput$pin"

  /**
   * The `uint1` value of this digital input.
   */
  override val out = new OutputPort[T](this) {

    override val description = "digital input value"

    override def getValue: String = s"$fctName();"
  }

  def getOutputs = Some(Seq(out))

  def getInputs = None



  override def getGlobalCode = Some(s"DigitalInput $valName($pin); // $out")

  override def getInitCode = {
      initialized()
      Some(s"$valName.initialize(); // Init of $this")
  }

  override def getLoopableCode = Some(s"$fctName();")

  override def getFunctionsDefinitions = {
    val res = new StringBuilder
    res ++= s"void $fctName() {"
    res ++= "if(valueHasChanged) {"
    res ++= s"${uint1().getType} val = $valName.read();"

    val in = ComponentManager.findConnections(out)
    for (inPort ← in)
      res ++= inPort.setInputValue("val") + "; // " + inPort

    res ++= "\n}}"
    Some(res.result())
  }

  override def getIncludeCode = Some("digitalinput.h")
}