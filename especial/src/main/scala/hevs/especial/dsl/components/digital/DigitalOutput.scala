package hevs.especial.dsl.components.digital

import hevs.especial.dsl.components.{In1, InputPort, hw_implemented}

case class DigitalOutput(override val pin: Int, defaultValue: Boolean = false) extends DigitalIO(pin) with In1 with
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
    // Initialize and set to OFF
    case true if !isInitialized =>
      initialized() // Init code called once only
      val res = new StringBuilder
      res ++= s"$valName.initialize(); // Init of $this\n"
      res ++= in.setInputValue(if(defaultValue) "true" else "false")
      Some(res.result())
    case _ => None
  }

  override def getIncludeCode = Some("#include \"digitaloutput.h\"")
}