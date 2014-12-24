package hevs.especial.dsl.components.core.logic

import hevs.especial.dsl.components._

import scala.reflect.runtime.universe._

/**
 * Inversion block use to invert a single input.
 *
 * The output value as the same type as the input. For [[bool]] values, the output is simply inverted. For all other
 * types, in the input "0", it is set to "1" and vice-versa.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @tparam T the type of the value to invert
 */
case class Not[T <: CType : TypeTag]() extends Component with In1 with Out1 with HwImplemented {

  override val description = s"NOT gate"

  /* I/O management */

  val in = new InputPort[T](this) {
    override val name = s"in"
    override val description = "input to invert"

    override def setInputValue(s: String) = {
      "" // FIXME: remove this ?
    }
  }

  val out = new OutputPort[T](this) {
    override val name = s"out"
    override val description = "inverted value"

    override def getValue: String = outValName() // inverted value stored in a local variable
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = Some(Seq(in))


  /* Code generation */

  override def getLoopableCode = {
    // Read the input
    val inValue = ComponentManager.findPredecessorOutputPort(in).getValue

    // Invert the value and store the result in a variable
    val sInvert = getTypeClass[T] match {
      case _: `Class`[bool] => s"!$inValue"
      case _ => s"""($inValue == 0) ? 1 : 0"""
    }
    Some(s"${getTypeString[T]} ${outValName()} = $sInvert;")
  }
}
