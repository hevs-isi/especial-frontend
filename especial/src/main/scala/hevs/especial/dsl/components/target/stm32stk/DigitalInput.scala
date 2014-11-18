package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components._

/**
 * Create a digital input for a specific pin.
 *
 * The value of this input is automatically read and save in the ISR (Interrupt Service Routine).
 *
 * @param pin GPIO pin
 */
case class DigitalInput(override val pin: Pin) extends DigitalIO(pin) with Out1 with HwImplemented {

  override val description = s"digital input\\non $pin"
  private val valName = s"digitalIn$getVarId"
  private val fctName = s"getlDigitalInput${pin.port}${pin.pinNumber}"

  /**
   * The `uint1` value of this digital input.
   */
  override val out = new OutputPort[T](this) {
    override val name = s"out"
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
    // Add a function to get the cached value of this input.
    val res = new StringBuilder

    // 1) Store the input value in a local variable
    res ++= s"void $fctName() {\n"
    res ++= "// Get the cached value (read from interrupt)\n"
    res ++= s"${bool().getType} val = $valName.get();\n"

    // 2) Propagate this value to all connected components
    val in = ComponentManager.findConnections(out)
    for (inPort ← in)
      res ++= inPort.setInputValue("val") + s"; //$inPort\n"

    res ++= "}"
    Some(res.result())
  }

  override def getIncludeCode = Seq("digitalinput.h")
}