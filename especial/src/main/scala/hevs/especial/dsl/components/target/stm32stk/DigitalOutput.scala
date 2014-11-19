package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{HwImplemented, In1, InputPort, Pin}

/**
 * Create a digital output for a specific pin.
 * Initialize the pin with a default value. The initialization of the output is done once only,
 * in the `initOutputs` function (nothing to do in the `init` function), because all outputs must be initialized first.
 *
 * @param pin the GPIO pin
 * @param initValue the default value of the output when initialized
 */
case class DigitalOutput(override val pin: Pin, initValue: Boolean = false) extends DigitalIO(pin) with In1 with
HwImplemented {

  override val description = s"digital output\\non $pin"

  private val valName = s"digitalOut$getVarId"
  private var initialized: Boolean = false

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

  override def getInitCode = {
    // Initialize the output in the `initOutputs` function. Do it only once !
    if (!initialized) {
      val res = new StringBuilder
      res ++= s"$valName.initialize(); // Init of $this"

      // Default output value is off. Set to on afterwards if necessary.
      if (initValue)
        res ++= "\n" + in.setInputValue(String.valueOf(initValue)) + ";"

      initialized = true // Init code called once only
      Some(res.result())
    }
    else {
      // Output already initialized in the `initOutputs` function.
      // Nothing to do in the `init` function.
      None
    }
  }

  override def getIncludeCode = Seq("digitaloutput.h")
}