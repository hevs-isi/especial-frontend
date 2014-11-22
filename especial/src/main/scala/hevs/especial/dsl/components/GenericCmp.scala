package hevs.especial.dsl.components

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Helper class used to build generic components more easily.
 *
 * @param nbrIn number of input to generate
 * @param nbrOut number of output to generate
 * @tparam S input type
 * @tparam T output type
 */
abstract class GenericCmp[S <: CType : TypeTag, T <: CType : TypeTag](nbrIn: Int, nbrOut: Int) extends Component {

  /* Generic I/O access */

  private val inputs = ListBuffer.empty[InputPort[S]]
  private val outputs = ListBuffer.empty[OutputPort[T]]

  protected def in(index: Int = 0) = selectIO(index, inputs)
  override def getInputs = if (inputs.size == 0) None else Some(inputs)
  protected def addCustomIn(in: InputPort[S]) = inputs += in

  protected def out(index: Int = 0) = selectIO(index, outputs)
  override def getOutputs = if (outputs.size == 0) None else Some(outputs)
  protected def addCustomOut(out: OutputPort[T]) = outputs += out

  /* Abstract functions */

  // Return null if not used (if nbrIn = 0)
  protected def setInputValue(index: Int, s: String): String

  // Return null if not used (if nbrIn = 0)
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
  private def selectIO[A](index: Int, io: ListBuffer[A]) = {
    // Print a custom message when an IndexOutOfBoundsException is thrown
    if (index < 0 || index >= io.size)
      throw new IndexOutOfBoundsException(s"Index $index does not exit. Index from [0 to ${io.size - 1}] only !")
    io(index)
  }

  private def createInput(index: Int) = new InputPort[S](this) {
    // 'in' - or - 'in1', 'in2', etc. if more than 1 input
    override val name = if (nbrIn > 1) s"in${index + 1}" else "in"
    override val description = if (nbrIn > 1) s"input ${index + 1}" else "input"

    override def setInputValue(s: String) = GenericCmp.this.setInputValue(index, s)
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

  createIO()
}
