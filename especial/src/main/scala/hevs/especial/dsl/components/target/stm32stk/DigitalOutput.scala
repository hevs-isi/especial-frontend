package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{HwImplemented, In1, InputPort, Pin}

/**
 * Create a digital output for a specific pin.
 *
 * @param pin GPIO pin
 * @param initValue the default value of the output when initialized
 */
case class DigitalOutput(override val pin: Pin, initValue: Boolean = false) extends DigitalIO(pin) with In1 with
HwImplemented {

  override val description = s"digital output\\non $pin"
  private val valName = s"digitalOut$getVarId"

  /**
   * The `uint1` value to write to this digital output.
   */
  override val in = new InputPort[T](this) {
    override val name = s"in"
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
      res ++= s"$valName.initialize(); // Init of $this"

      // Default output value is off. Set to on afterwards if necessary.
      if(initValue)
        res ++= in.setInputValue(String.valueOf(initValue)) + ";"

      Some(res.result())
    case _ => None
  }

  override def getIncludeCode = Seq("digitaloutput.h")
}