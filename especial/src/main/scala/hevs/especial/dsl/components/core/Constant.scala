package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.reflect.runtime.universe._

/**
 * Generate a constant value.
 *
 * The component has a single output. Once the component is initialized, the value of the constant cannot be changed
 * anymore. No input available. The constant value is simply paste in the generated code when needed.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @param value the value of the constant to generate. Cannot be modified.
 * @tparam T the type of the constant
 */
case class Constant[T <: CType : TypeTag](value: T) extends Component with Out1 with HwImplemented {

  override val description = s"constant generator\\n(${value.v})"

  /* I/O management */

  val out = new OutputPort[T](this) {
    override val name = s"out"
    override val description = "the constant value"

    // Constant propagation improvement. See issue #13.
    // override val isConstant = true

    // Print the value as a String (hardcoded because it cannot change)
    override def getValue: String = String.valueOf(value.v)
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = None // No input. The constant value cannot be modified.
}