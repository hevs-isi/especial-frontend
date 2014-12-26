package hevs.especial.dsl.components

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Helper class used to build generic components more easily.
 *
 * Create more easily components with multiple inputs and outputs. All inputs and all outputs have the same type.
 * Custom inputs or outputs can also be added manually.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @param nbrIn number of input to generate
 * @param nbrOut number of output to generate
 * @tparam S input type
 * @tparam T output type
 */
abstract class GenericCmp[S <: CType : TypeTag, T <: CType : TypeTag](nbrIn: Int, nbrOut: Int) extends Component {

  /* Generic I/O access */
  private val inputs = ListBuffer.empty[InputPort[CType]]
  private val outputs = ListBuffer.empty[OutputPort[CType]]

  protected def in(index: Int = 0) = selectIO(index, inputs).asInstanceOf[InputPort[S]]
  override def getInputs = if (inputs.size == 0) None else Some(inputs.toSeq)
  protected def addCustomIn[A <: CType](in: InputPort[A]) = inputs += in

  protected def out(index: Int = 0) = selectIO(index, outputs).asInstanceOf[OutputPort[T]]
  override def getOutputs = if (outputs.size == 0) None else Some(outputs.toSeq)
  protected def addCustomOut[A <: CType](out: OutputPort[A]) = outputs += out

  /* Abstract function */

  /**
   * Return the value of the corresponding output.
   *
   * @param index the index of the output (from 0 to nbrOut - 1)
   * @return the value of the output (C code as a String)
   */
  protected def getOutputValue(index: Int): String


  /* Private helper functions */

  /**
   * Select and input of the component.
   * Check if the index is valid or not.
   *
   * @throws IndexOutOfBoundsException if the index does not exist
   * @param index element index
   * @return the corresponding element to connect if index is valid
   */
  private def selectIO(index: Int, io: ListBuffer[_]) = {
    // Print a custom message when an IndexOutOfBoundsException is thrown
    if (index < 0 || index >= io.size)
      throw new IndexOutOfBoundsException(s"Index $index does not exit. Index from [0 to ${io.size - 1}] only !")
    io(index)
  }

  private def createInput(index: Int) = new InputPort[S](this) {
    // 'in' - or - 'in1', 'in2', etc. if more than 1 input
    override val name = if (nbrIn > 1) s"in${index + 1}" else "in"
    override val description = if (nbrIn > 1) s"input ${index + 1}" else "input"

    override def setInputValue(s: String) = {
      "" // FIXME: remove this ?
    }
  }

  private def createOutput(index: Int) = new OutputPort[T](this) {
    // 'out' - or - 'out1', 'out2', etc. if more than 1 output
    override val name = if (nbrOut > 1) s"out${index + 1}" else "out"
    override val description = if (nbrOut > 1) s"output ${index + 1}" else "output"

    override def getValue = GenericCmp.this.getOutputValue(index)
  }

  private def createIO(): Unit = {
    for {
      i <- 0 until nbrIn
      in = createInput(i)
      if in != null
    }
      inputs += in

    for {
      i <- 0 until nbrOut
      out = createOutput(i)
      if out != null
    }
      outputs += out
  }

  createIO() // Create generic inputs and outputs
}
