package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components._

/**
 * Create a digital output for a specific pin.
 *
 * Initialize the pin with a default value. The initialization of the output is done once only,
 * in the `initOutputs` function (nothing to do in the `init` function), because all outputs must be initialized first.
 *
 * @param pin the pin of the GPIO (port and pin number)
 * @param initValue the default value of the output when initialized
 */
class DigitalOutput private(private val pin: Pin, initValue: Boolean = false) extends
  Gpio(pin) with In1 with HwImplemented {

  override val description = s"digital output\\non $pin"

  private val valName = outValName()
  private var initialized: Boolean = false

  /* I/O management */

  /**
   * The `bool` value to write to this digital output.
   */
  override val in = new InputPort[bool](this) {
    override val name = s"in"
    override val description = "digital output value"

    override def setInputValue(s: String): String = s"$valName.set($s)"
  }

  override def getOutputs = None

  override def getInputs = Some(Seq(in))

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

/**
 * Create a digital output for a specific pin.
 *
 * The output pin should be unique. If is not possible to create two output for the same pin.
 * The [[DigitalOutput]] constructor is private and a [[DigitalOutput]] must be created using this companion object to
 * be sure than only one output exist for this pin.
 */
object DigitalOutput {

  /**
   * Create a digital output for a specific pin.
   *
   * @param pin the pin of the GPIO (port and pin number)
   * @param initValue the default value of the output when initialized
   * @return the digital output or the existing one if already in the graph
   */
  def apply(pin: Pin, initValue: Boolean = false): DigitalOutput = {
    val tmpCmp = new DigitalOutput(pin, initValue)
    // Check of the output already exist in the graph.
    // If yes, return the existing component. If no, return a new component.
    val isAdded = ComponentManager.addComponent(tmpCmp)

    // Return the existing component if defined or the new added component
    isAdded.getOrElse(tmpCmp)
  }
}