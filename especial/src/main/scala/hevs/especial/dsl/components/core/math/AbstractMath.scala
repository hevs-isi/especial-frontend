package hevs.especial.dsl.components.core.math

import hevs.especial.dsl.components._

import scala.reflect.runtime.universe._

/**
 * Helper class to create generic math components.
 *
 * Create math block with a generic number of inputs and one output. Input(s) and output types have the same type.
 * Useful to create addition, subtraction, multiplication, division, min, max blocks, etc.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
abstract class AbstractMath[T <: CType : TypeTag](nbrIn: Int) extends GenericCmp[T, T](nbrIn, 1)
with HwImplemented with Out1 {

  /* I/O management */

  // Single output connected here
  override val out: OutputPort[T] = out(0)

  /**
   * Return the value of the corresponding output.
   * The value of the output is computed by the block implementation and the result must be stored in a local variable.
   *
   * @return the value of the output (stored in a variable)
   */
  protected override def getOutputValue(index: Int) = {
    // Only one output, index not used
    assert(index == 0)
    outValName()
  }

  /**
   * Read the value of each inputs of the block.
   * @return a sequence of all input values
   */
  protected def readInputs(): Seq[String] = {
    // Read all input values
    for (i <- 0 until nbrIn) yield {
      val port = in(i)
      ComponentManager.findPredecessorOutputPort(port).getValue
    }
  }
}