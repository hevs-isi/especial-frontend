package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components._

/**
 * Create an analog for a specific pin.
 *
 * Use an A/D converter to read an analog input value. The channel used for the conversion must be specified.
 * The converted value is saved in [[uint16]]] format.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @param pin the pin of the GPIO (port and pin number)
 * @param channel the A/D channel used for the conversion
 */
class AnalogInput private(private val pin: Pin, private val channel: Int) extends Gpio(pin)
  with Out1 with HwImplemented {

  override val description = s"analog input\\non $pin"

  /* I/O management */

  /**
   * The analog value converted to a `uint16` digital value.
   */
  override val out = new OutputPort[uint16](this) {
    override val name = s"out"
    override val description = "analog input value"

    // varName contains the output value
    override def getValue: String = s"$varName"
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = None


  /* Code generation */

  private val fctName = s"getlAnalogInput${pin.port}${pin.pinNumber}"
  private val valName = inValName()
  private val varName = s"in_${pin.port}${pin.pinNumber}"

  override def getIncludeCode = Seq("analoginput.h")

  override def getGlobalCode = Some(s"AnalogInput $valName($pinName, $channel); // $out")

  override def getInitCode = Some(s"$valName.initialize(); // Init of $this")

  override def getFunctionsDefinitions = {
    // Add a function to get the cached value of this input.
    val res = new StringBuilder
    res ++= s"${uint16().getType} $fctName() {\n"
    res ++= "// Start an A/D conversion and wait for the result\n"
    res ++= s"return $valName.read();\n"
    res ++= "}"
    Some(res.result())
  }

  override def getLoopableCode = {
    // Store the input value in a local variable
    Some(s"${uint16().getType} $varName = $fctName();")
  }
}

/**
 * Create a analog input for a specific pin.
 *
 * The input pin should be unique. If is not possible to create two analog input for the same pin.
 * The [[AnalogInput]] constructor is private and a [[AnalogInput]] must be created using this companion object to
 * be sure than only one output exist for this pin.
 */
object AnalogInput {

  /**
   * Create an analog input for a specific pin.
   *
   * @param pin the pin of the GPIO (port and pin number)
   * @param channel the A/D channel used for the conversion
   * @return the analog input or the existing one if already in the graph
   */
  def apply(pin: Pin, channel: Int): AnalogInput = {
    val tmpCmp = new AnalogInput(pin, channel)
    // Check of the output already exist in the graph.
    // If yes, return the existing component. If no, return a new component.
    val isAdded = ComponentManager.addComponent(tmpCmp)

    // Return the existing component if defined or the new added component
    isAdded.getOrElse(tmpCmp)
  }
}
