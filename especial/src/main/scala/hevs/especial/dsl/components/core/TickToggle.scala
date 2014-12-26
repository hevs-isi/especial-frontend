package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.reflect.runtime.universe._

object TickToggle {

  def apply[T <: CType : TypeTag](input: OutputPort[T]) = {
    val c = new TickToggle[T]
    input --> c.in
    c
  }
}

/**
 * Toggle the output value on each loop iteration.
 * Used for test purposes only. Useful to generate different values for each loop iterations.
 *
 * The initial value is inverted and then set as output.
 * The output value as the same type as the input. For [[bool]] values, the output is simply inverted. For all other
 * types, if the input "0", the output is "1" and vice-versa.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @tparam T the input and output type
 */
case class TickToggle[T <: CType : TypeTag]() extends Component with Out1 with HwImplemented {

  override val description = s"tick toggle generator"

  /* I/O management */

  val in = new InputPort[T](this) {
    override val name = s"in"
    override val description = "inverted value"
  }

  val out = new OutputPort[T](this) {
    override val name = s"out"
    override val description = "toggle value"

    // Return the state of the component stored in a global variable
    override def getValue: String = s"$valName"
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = Some(Seq(in))


  /* Code generation */

  private val valName: String = outValName()

  override def getBeginOfMainAfterInit = {
    // State of the toggle generator. Init with the default state.
    val inValue = ComponentManager.findPredecessorOutputPort(in).getValue
    Some(s"${getTypeString[T]} $valName = $inValue; // $out")
  }

  override def getLoopableCode = {
    // Invert the global variable value
    val sInvert = getTypeClass[T] match {
      case _: `Class`[bool] => s"!$valName"
      case _ => s"""($valName == 0) ? 1 : 0"""
    }
    Some(s"$valName = $sInvert;")
  }
}