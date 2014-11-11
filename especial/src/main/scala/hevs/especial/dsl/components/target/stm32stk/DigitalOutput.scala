package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{In1, InputPort, Pin, hw_implemented}

/**
 * Create a digital output for a specific pin.
 *
 * @param pin GPIO pin
 * @param initValue the default value of the output when initialized
 */
case class DigitalOutput(override val pin: Pin, initValue: Boolean = false) extends DigitalIO(pin) with In1 with
hw_implemented {

  override val description = s"digital output on '$pin'"
  override val valName = s"digitalOut$getVarId"

  /**
   * The `uint1` value to write to this digital output.
   */
  override val in = new InputPort[T](this) {
    override val description = "digital output value"
    override def setInputValue(s: String): String = s"$valName.set($s)"
  }

  def getOutputs = None

  def getInputs = Some(Seq(in))

  /* Code generation */

  override def getGlobalCode = Some(s"DigitalOutput $valName($pinName); // $in")

  override def getInitCode = !isInitialized match {
    // Initialize and set the output to the default value (false if not specified)
    case true if !isInitialized =>
      initialized() // Init code called once only
    val res = new StringBuilder
      res ++= s"$valName.initialize(); // Init of $this\n"
      res ++= in.setInputValue(if (initValue) "true" else "false") + ";"
      Some(res.result())
    case _ => None
  }

  override def getIncludeCode = Some("digitaloutput.h")
}