package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components._

/**
 * Create a digital input for a specific pin.
 *
 * The value of this input is automatically read and save in the ISR (Interrupt Service Routine).
 *
 * @param pin GPIO pin
 */
case class DigitalInput(override val pin: Pin) extends DigitalIO(pin) with Out1 with hw_implemented {

  override val description = s"digital input on '$pin'"
  override val valName = s"digitalIn$getVarId"
  private val fctName = s"pollDigitalInput${pin.port}${pin.pinNumber}"

  /**
   * The `uint1` value of this digital input.
   */
  override val out = new OutputPort[T](this) {
    override val description = "digital input value"
    override def getValue: String = s"$fctName();"
  }

  def getOutputs = Some(Seq(out))

  def getInputs = None

  /* Code generation */

  override def getGlobalCode = Some(s"DigitalInput $valName($pinName); // $out")

  override def getInitCode = {
    initialized()
    val res = new StringBuilder
    res ++= s"$valName.initialize(); // Init of $this\n"
    res ++= s"$valName.registerInterrupt(); // Use interrupts"
    Some(res.result())
  }

  override def getLoopableCode = Some(s"$fctName();")

  override def getFunctionsDefinitions = {
    val res = new StringBuilder
    res ++= s"void $fctName() {"
    res ++= "// Get the cached value (read from interrupt)"
    res ++= s"${uint1().getType} val = $valName.get();"

    val in = ComponentManager.findConnections(out)
    for (inPort ← in)
      res ++= inPort.setInputValue("val") + "; // " + inPort

    res ++= "\n}"
    Some(res.result())
  }

  override def getIncludeCode = Seq("digitalinput.h")
}