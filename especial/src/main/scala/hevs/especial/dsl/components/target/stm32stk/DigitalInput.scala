package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components._

/**
 * Create a digital input for a specific pin.
 *
 * The value of this input is automatically read and save in the ISR (Interrupt Service Routine).
 * Interrupts are used by default with this implementation.
 * The input is initialized in the `ìnit` function when needed (once only).
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @param pin the pin of the GPIO (port and pin number)
 */
class DigitalInput private(private val pin: Pin) extends Gpio(pin) with Out1 with HwImplemented {

  override val description = s"digital input\\non $pin"


  /* I/O management */

  /**
   * The `uint1` value of this digital input.
   */
  override val out = new OutputPort[bool](this) {
    override val name = s"out"
    override val description = "digital input value"

    // varName contains the output value
    override def getValue: String = s"$varName"
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = None


  /* Code generation */

  private val valName = inValName()
  private val varName = s"in_${pin.port}${pin.pinNumber}"

  override def getGlobalCode = Some(s"DigitalInput $valName($pinName); // $out")

  override def getInitCode = {
    val res = new StringBuilder
    res ++= s"$valName.initialize(); // Init of $this"
    // Enabled by default
    // res ++= s"$valName.registerInterrupt(); // Use interrupts"
    Some(res.result())
  }

  override def getLoopableCode = {
    // Store the input value in a local variable
    Some(s"${bool().getType} $varName = $valName.get();")
  }

  override def getIncludeCode = Seq("digitalinput.h")
}

/**
 * Create a digital input for a specific pin.
 *
 * The input pin should be unique. If is not possible to create two inputs for the same pin.
 * The [[DigitalInput]] constructor is private and a [[DigitalInput]] must be created using this companion object to
 * be sure than only one input exist for this pin.
 */
object DigitalInput {

  /**
   * Create a digital input for a specific pin.
   *
   * @param pin the pin of the GPIO (port and pin number)
   * @return the digital input or the existing one if already in the graph
   */
  def apply(pin: Pin): DigitalInput = {
    val tmpCmp = new DigitalInput(pin)
    // Check of the input already exist in the graph.
    // If yes, return the existing component. If no, return a new component.
    val isAdded = ComponentManager.addComponent(tmpCmp)

    // Return the existing component if defined or the new added component
    isAdded.getOrElse(tmpCmp)
  }
}