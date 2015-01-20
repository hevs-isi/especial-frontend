package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components._
import hevs.especial.utils.Settings

/**
 * Specific digital input, used to count input pulses.
 *
 * Rising and falling edges are automatically counted using and ISR (Interrupt Service Routine).
 * Similar behaviour as a [[DigitalInput]], but the output type is an [[uint32]] value.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @param pin the pin of the GPIO (port and pin number)
 */
class PulseInputCounter private(private val pin: Pin) extends Gpio(pin) with Out1 with HwImplemented {

  override val description = s"pulse counter\\non $pin"


  /* I/O management */

  /**
   * The [[uint32]] value of this input.
   */
  override val out = new OutputPort[uint32](this) {
    override val name = s"out"
    override val description = "pulse counter"

    // varName contains the output value
    override def getValue: String = s"$varName"
  }

  override def getOutputs: Some[Seq[OutputPort[uint32]]] = Some(Seq(out))

  override def getInputs = None


  /* Code generation */

  private val valName = inValName()
  private val varName = s"in_${pin.port}${pin.pinNumber}"

  override def getGlobalCode = {
    val res = s"PulseInput $valName($pinName);"
    if (Settings.GEN_VERBOSE_CODE)
      Some(res + s"\t\t// $out") // Print a description of the input
    else
      Some(res)
  }

  override def getInitCode = {
    val res = new StringBuilder
    res ++= s"$valName.initialize();"
    Some(res.result())
  }

  override def getLoopableCode = {
    // Store the input value in a local variable
    // get() or getSpeed() are the same
    Some(s"${uint32().getType} $varName = $valName.get();")
  }

  override def getIncludeCode = Seq("pulseinput.h")
}

/**
 * Create a pulse input counter for a specific pin.
 *
 * The input pin should be unique. If is not possible to create two inputs for the same pin.
 * The [[PulseInputCounter]] constructor is private and a [[PulseInputCounter]] must be created using this companion
 * object to be sure than only one input exist for this pin.
 */
object PulseInputCounter {

  /**
   * Create a pulse input counter for a specific pin.
   *
   * @param pin the pin of the GPIO (port and pin number)
   * @return the pulse counter or the existing one if already in the graph
   */
  def apply(pin: Pin): PulseInputCounter = {
    val tmpCmp = new PulseInputCounter(pin)
    // Check of the input already exist in the graph.
    // If yes, return the existing component. If no, return a new component.
    val isAdded = ComponentManager.addComponent(tmpCmp)

    // Return the existing component if defined or the new added component
    isAdded.getOrElse(tmpCmp)
  }
}