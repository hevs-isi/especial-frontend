package hevs.especial.dsl.components.core.logic

import hevs.especial.dsl.components._
import hevs.especial.utils.Settings

/**
 * Toggle the boolean output when a rising edge is detected on the enable input.
 * The initial output value can be configured.
 *
 * @version 1.0
 * @author Christopher Métrailler (mei@hevs.ch)
 */
class ToggleBoolean private(initValue: Boolean) extends Component with Out1 with HwImplemented {

  override val description = s"toggle boolean\\n(default is ${String.valueOf(initValue)})"

  /* I/O management */

  /**
   * The [[bool]] input value to enable the toggle.
   */
  val en = new InputPort[bool](this) {
    override val name = s"en"
    override val description = "enable input"
  }

  /**
   * The toggled [[bool]] output value.
   */
  override val out = new OutputPort[bool](this) {
    override val name = s"out"
    override val description = "toggled output value"

    // outName variable contains the output value
    override def getValue: String = s"$outName"
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = Some(Seq(en))

  /* Code generation */

  // Variables names
  private val lastInName = valName("last")
  private val outName = outValName()

  override def getGlobalCode = {
    val t = getTypeString[bool]
    val init = String.valueOf(initValue)
    val res = s"$t $lastInName = false;\n$t $outName = $init;"
    if (Settings.GEN_VERBOSE_CODE)
      Some(res + "\t" * 4 + s"// $en") // Print a description of the output
    else
      Some(res)
  }

  override def getLoopableCode = {
    // Read the enable input pin value
    val enValue = ComponentManager.findPredecessorOutputPort(en).getValue

    val ret = new StringBuilder
    ret ++= s"if($enValue && !$lastInName)\n" // Rising edge detection
    ret ++= s"$outName = !$outName;\n"
    ret ++= s"$lastInName = $enValue;" // Store the last en value
    Some(ret.result())
  }
}

object ToggleBoolean {

  /**
   * Create a toggle boolean gate. Default output value is `false`.
   * When a rising edge is detected on the enable input, the boolean output is toggled.
   *
   * @param initValue the initial output value
   * @return the toggle boolean component
   */
  def apply(initValue: Boolean = false) = new ToggleBoolean(initValue)
}
