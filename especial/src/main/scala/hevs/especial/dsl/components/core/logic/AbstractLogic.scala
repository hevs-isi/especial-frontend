package hevs.especial.dsl.components.core.logic

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer

/**
 * Generic logic gate. Deal with Boolean (`bool`) values.
 * Compute different logic operations, on a generic number of input to one output.
 *
 * @param nbrIn The number of generic input of the component
 * @param operator Boolean operator to compute
 */
abstract class AbstractLogic(val nbrIn: Int, operator: String) extends
GenericCmp[bool, bool](nbrIn, 1) with HwImplemented with Out1 {

  def this(nbrIn: Int, operator: String, inputs: OutputPort[bool]*) = {
    this(nbrIn, operator)

    // Automatically connect component inputs.
    // Ignore if too much or too less parameters are given.
    for (i <- 0 until nbrIn) {
      if (inputs.indices.contains(i))
        inputs(i) --> in(i) // FIXME: check this !!
    }
  }

  /* I/O management */

  protected def setInputValue(index: Int, s: String) = s"${inValName(index)} = $s"

  protected def getOutputValue(index: Int) = getOutputValue // Only one output, index not used

  protected def getOutputValue: String = {
    // Print the boolean operation with all inputs
    // Example: in1_comp2 & in2_comp2 & ...
    val inputs = for (i <- 0 until nbrIn) yield inValName(i)
    inputs.mkString(s" $operator ")
  }

  // Single output connected here
  override val out: OutputPort[bool] = out(0)


  /* Code generation */

  private val tpe = bool().getType

  override def getGlobalCode = {
    // Input variables declarations for the gate
    // Example: bool_t in1_comp3, in2_comp3, in3_comp3;
    val inputs = for (i <- 0 until nbrIn) yield inValName(i)
    Some(s"$tpe ${inputs.mkString(", ")}; // $this")
  }


  override def getLoopableCode = {
    val result: ListBuffer[String] = ListBuffer()

    // Compute the result from the global variables
    result += s"const $tpe ${outValName()} = ${out(0).getValue}; // ${out(0)}"

    // Set the output value to connected components
    for (inPort ← ComponentManager.findConnections(out(0)))
      result += inPort.setInputValue(s"${outValName()}") + "; // " + inPort

    Some(result.mkString("\n"))
  }
}
