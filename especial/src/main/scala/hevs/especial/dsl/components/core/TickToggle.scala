package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

case class TickToggle(initValue: Boolean = false) extends Component with Out1 with HwImplemented {

  override val description = s"tick toggle generator"
  private val valName: String = outValName()

  /* I/O management */

  val out = new OutputPort[bool](this) {
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
    val sInitVal = if(initValue) "true" else "false"
    Some(s"${bool().getType} $valName = $sInitVal; // $out")
  }

  override def getInitCode = {
    // TODO: init all outputs here
    None
  }

  override def getLoopableCode = {
    // Propagate the output value to connected inputs
    val in = ComponentManager.findConnections(out)
    val results: ListBuffer[String] = ListBuffer()
    for (inPort <- in)
      results += inPort.setInputValue(out.getValue) + "; // " + inPort

    // Final invert the state of the generator output
    val invert = s"\n$valName = !$valName; // Invert $out"
    Some(results.mkString("\n") + invert)
  }
}