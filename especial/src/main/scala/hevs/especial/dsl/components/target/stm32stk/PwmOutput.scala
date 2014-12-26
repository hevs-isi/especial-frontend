package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components._

/**
 * Create a Pulse-width modulation (PWM) output for a specific pin.
 * Initialize the output and set it to `OFF` (duty cycle 0%) by default.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @param pin the pin of the GPIO (port and pin number)
 */
class PwmOutput private(private val pin: Pin) extends Gpio(pin) with In1 with HwImplemented {

  override val description = s"PWM output\\non $pin"

  private val valName = outValName()
  private var initialized: Boolean = false

  /* I/O management */

  /**
   * The `uint16` period value to write to this PWM output.
   */
  override val in = new InputPort[uint16](this) {
    override val name = s"in"
    override val description = "PWM output value"
  }

  override def getOutputs = None

  override def getInputs = Some(Seq(in))


  /* Code generation */

  override def getIncludeCode = Seq("pwmoutput.h")

  override def getGlobalCode = Some(s"PwmOutput $valName($pinName);\t\t// $in")

  override def getInitCode = {
    // Initialize the output in the `initOutputs` function. Do it only once !
    if (!initialized) {
      initialized = true // Init code called once only
      Some(s"$valName.initialize();")
    }
    else {
      // Output already initialized in the `initOutputs` function.
      // Nothing to do in the `init` function.
      None
    }
  }

  override def getLoopableCode = {
    val inValue = ComponentManager.findPredecessorOutputPort(in).getValue
    Some(s"$valName.setPeriod($inValue);")
  }
}

/**
 * Create a PWM output for a specific pin.
 *
 * The output pin should be unique. If is not possible to create two PWM output for the same pin.
 * The [[PwmOutput]] constructor is private and a [[PwmOutput]] must be created using this companion object to
 * be sure than only one output exist for this pin.
 */
object PwmOutput {

  /**
   * Create a PWM output input for a specific pin.
   *
   * @param pin the pin of the GPIO (port and pin number)
   * @return the PWM output or the existing one if already in the graph
   */
  def apply(pin: Pin): PwmOutput = {
    val tmpCmp = new PwmOutput(pin)
    // Check of the output already exist in the graph.
    // If yes, return the existing component. If no, return a new component.
    val isAdded = ComponentManager.addComponent(tmpCmp)

    // Return the existing component if defined or the new added component
    isAdded.getOrElse(tmpCmp)
  }
}
