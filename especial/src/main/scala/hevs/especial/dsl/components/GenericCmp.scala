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
abstract class GenericCmp[S <: CType: TypeTag, T <: CType: TypeTag](nbrIn: Int, nbrOut: Int) extends Component {

  /* Generic I/O access */

  private val inputs = ListBuffer.empty[InputPort[S]]
  private val outputs = ListBuffer.empty[OutputPort[T]]

  override def getInputs = if (inputs.size == 0) None else Some(inputs)
  protected def in(index: Int) = selectIO(index, inputs).asInstanceOf[InputPort[S]]
  protected def addCustomIn(in: InputPort[S]) = inputs += in

  protected def out(index: Int) = selectIO(index, outputs).asInstanceOf[InputPort[S]]
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
  private def selectIO(index: Int, io: ListBuffer[_]) = {
    if (io.size == 0)
      throw new IndexOutOfBoundsException(s"'$io' is empty !")
    if (index < 0 || index > io.size)
      throw new IndexOutOfBoundsException(s"Index $index does not exit for '$io' !")
    io(index)
  }

  private def createInput(index: Int) = new InputPort[S](this) {
    override val name = s"in${index + 1}"
    override val description = s"input ${index + 1}"

    override def setInputValue(s: String) =  GenericCmp.this.setInputValue(index, s)
  }

  private def createOutput(index: Int) = new OutputPort[T](this) {
    override val name = s"out${index + 1}"
    override val description = s"output ${index + 1}"

    override def getValue = GenericCmp.this.getOutputValue(index)
  }

  private def createIO() = {
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
