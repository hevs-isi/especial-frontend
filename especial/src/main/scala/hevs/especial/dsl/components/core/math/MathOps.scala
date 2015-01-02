package hevs.especial.dsl.components.core.math

import hevs.especial.dsl.components.{CType, OutputPort}

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Compute standard math operations from a generic number of inputs.
 *
 * An operation is computed from input0 with input1 with input2, etc.
 * Example: for an adder with 2 inputs: `cmp_out = cmp_in1 + cmp_in2;`
 *
 * A variadic constructor is available to easily connect inputs automatically.
 * Warning: inputs and outputs have the same type, check overflows !
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @param nbrIn the number of input of the math block
 * @param operator the math operator (add, mul, div, sub, etc.)
 * @tparam T Input and output type of the math block
 */
abstract class MathOps[T <: CType : TypeTag](val nbrIn: Int, operator: String) extends AbstractMath[T](nbrIn) {

  /**
   * Helper constructor to connected a list of inputs easily.
   *
   * The list of [[OutputPort]] are automatically connected to the component inputs. It is possible to connect only
   * some inputs and the other manually. It too much input are provided, they are simply ignored.
   *
   * @param nbrIn the number of input of the math block
   * @param operator the math operator (add, mul, div, sub, etc.)
   * @param inputs list of output ports to connect. The first input is input number 0, then 1, etc.
   */
  def this(nbrIn: Int, operator: String, inputs: OutputPort[T]*) = {
    this(nbrIn, operator)

    // Automatically connect component inputs.
    // Ignore if too much or too less parameters are given.
    for (i <- 0 until nbrIn) {
      if (inputs.indices.contains(i))
        inputs(i) --> in(i)
    }
  }


  /* Code generation */

  private val tpe = getTypeString[T] // I/O type

  override def getLoopableCode = {
    // Read inputs opf the block
    val inValues = readInputs()

    // Compute the result of the math block
    // Example: "uint16_t out_cmp09 = 4096 / 4;"
    val result = ListBuffer.empty[String]
    result += s"$tpe ${outValName()} = ${inValues.mkString(s" $operator ")};"
    Some(result.mkString("\n"))
  }
}