package hevs.especial.dsl.components.core.math

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Generic math component with a generic number of input and one output.
 *
 * Input(s) and output types are the same. This is an helper class used to create easily addition, subtraction,
 * multiplication, division, min, max blocks, etc.
 */
abstract class AbstractMath[T <: CType : TypeTag](nbrIn: Int) extends GenericCmp[T, T](nbrIn, 1)
  with HwImplemented with Out1 {

  /* I/O management */

  protected def setInputValue(index: Int, s: String) = s"${inValName(index)} = $s"

  protected def getOutputValue(index: Int) = getOutputValue // Only one output, index not used

  protected def getOutputValue: String

  // Single output connected here
  override val out: OutputPort[T] = out(0)


  /* Code generation */

  private val tpe = getTypeString[T]  // I/O type

  override def getGlobalCode = {
    // Input variables declarations of the block (generic number)
    // Example: uint16_t in1_comp4, in2_comp4;
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