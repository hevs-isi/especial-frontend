package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * The constant component can be used to generate a fixed value.
 * The component has a single output. Once the component is initialized, the value of the constant cannot be changed
 * anymore. No input available.
 *
 * @param value teh value of the constant to generate. Cannot be modified.
 * @tparam T the type of the constant
 */
case class Constant[T <: CType : TypeTag](value: T) extends Component with Out1 with HwImplemented {

  override val description = s"constant generator\\n(${value.v})"

  /* I/O management */

  val out = new OutputPort[T](this) {
    override val name = s"out"
    override val description = "the constant value"

    // Print the value as a String (hardcoded because it cannot change)
    override def getValue: String = String.valueOf(value.v)
  }

  override def getOutputs= Some(Seq(out))

  // No input. The constant value cannot be modified.
  override def getInputs = None

  /* Code generation */

  override def getInitCode = {
    // Each component has to propagate its output value to all connected components
    val in = ComponentManager.findConnections(out)
    val results = ListBuffer.empty[String]
    for (inPort <- in)
      results += inPort.setInputValue(out.getValue) + "; // " + inPort
    Some(results.mkString("\n"))
  }
}