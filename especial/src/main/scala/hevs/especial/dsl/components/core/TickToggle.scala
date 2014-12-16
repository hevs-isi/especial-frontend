package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Toggle the output value on each while loop.
 * Used for test purposes only. Useful to generate different values for each loop.
 *
 * Boolean value are inverted (true/false). All other value are 1 or 0.
 *
 * @param initValue the first value available as output
 * @tparam T the output type
 */
case class TickToggle[T <: CType : TypeTag] (initValue: T) extends Component with Out1 with HwImplemented {

  override val description = s"tick toggle generator\\n(${initValue.v})"
  private val valName: String = outValName()

  /* I/O management */

  val out = new OutputPort[T](this) {
    override val name = s"out"
    override val description = "toggle value"

    // Return the state of the component stored in a global variable
    override def getValue: String = valName
  }

  override def getOutputs= Some(Seq(out))

  override def getInputs = None

  /* Code generation */

  override def getGlobalCode = {
    // State of the toggle generator. Init with the default state.
    // Not necessary to call the init function...
    val sVal = String.valueOf(initValue.v)
    Some(s"${initValue.getType} $valName = $sVal; // $out")
  }

  override def getInitCode = {
    // TODO: init all outputs here
    None
  }

  override def getLoopableCode = {
    // Propagate the output value to connected inputs
    val in = ComponentManager.findConnections(out)
    val results = ListBuffer.empty[String]
    for (inPort <- in)
      results += inPort.setInputValue(out.getValue) + "; // " + inPort

    // Finally invert the generator output
    val sInvert = initValue match {
      case v: bool => s"!$valName"
      case _ => s"""($valName == 0) ? 1 : 0"""
    }

    val invert = s"\n$valName = $sInvert; // Invert $out"
    Some(results.mkString("\n") + invert)
  }
}