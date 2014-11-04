package hevs.especial.dsl.components.digital

import hevs.especial.dsl.components.{In1, InputPort, hw_implemented}

/**
 * Create a digital output on a specified pin number.
 *
 * @param pin GPIO pin number
 * @param init the default value of the output when initialized
 */
case class DigitalOutput(override val pin: Int, init: Boolean = false) extends DigitalIO(pin) with In1 with
hw_implemented {

  override val description = s"digital output on pin $pin"

  private val valName = s"digitalOut$getVarId" // unique variable name

  /**
   * The `uint1` value to write to this digital output.
   */
  override val in = new InputPort[T](this) {

    override val description = "digital output value"

    override def setInputValue(s: String): String = s"$valName.set($s)"
  }

  def getOutputs = None

  def getInputs = Some(Seq(in))

  override def getGlobalCode = Some(s"DigitalOutput $valName($pin); // $in")

  override def getInitCode = !isInitialized match {
    // Initialize and set the output to OFF
    case true if !isInitialized =>
      initialized() // Init code called once only
    val res = new StringBuilder
      res ++= s"$valName.initialize(); // Init of $this\n"
      res ++= in.setInputValue(if (init) "true" else "false") + ";"
      Some(res.result())
    case _ => None
  }

  override def getIncludeCode = Some("digitaloutput.h")
}