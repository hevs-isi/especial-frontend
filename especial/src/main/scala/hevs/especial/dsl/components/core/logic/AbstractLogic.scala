package hevs.especial.dsl.components.logic

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.fundamentals._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Generic logic gate. Deal with boolean (uint1) values. Compute different logic operations,
 * on a generic number of input to one output.
 * @param nbrInput number of inputs of the component
 * @param operator Boolean operator to compute
 */
abstract class AbstractLogic(nbrInput: Int, operator: String) extends Component with hw_implemented {

  type T = uint1 // Inputs are outputs are boolean values

  // One single boolean output
  val out = new OutputPort[T](this) {
    override val description = "the AND output"

    // Compute the AND value as output from all inputs (as global variables)
    override def getValue: String = {
      val inputs = for (i <- 1 to nbrInput) yield inputVarName(i)
      inputs.mkString(s" $operator ") // Example: in1Comp2 & in2Comp2 & ...
    }
  }
  /**
   * List of inputs. Use the `in` method to access to it safely.
   */
  private val inputs = mutable.ListBuffer.empty[InputPort[uint1]]
  private val tpe = uint1().getType

  // Create input(s) of the component
  createInputs()
  private val outputVarName = s"outComp$getId"

  def getOutputs = Some(Seq(out))

  def getInputs = Some(inputs)

  /**
   * Select and input of the component. Input index start at 1. Check if the index is valid or not.
   * @throws IndexOutOfBoundsException if the input does not exist
   * @param index input number, start at 1
   * @return the corresponding input to connect if index is valid
   */
  def in(index: Int) = {
    val nbr = index - 1
    if (nbr < 0 || nbr >= inputs.size)
      throw new IndexOutOfBoundsException(s"Input $index does not exit. Goes from 1 to ${inputs.size} !")
    inputs(index - 1)
  }

  override def getGlobalCode = out.isConnected match {
    // Input variables declarations of the gate
    case true =>
      val inputs = for (i <- 1 to nbrInput) yield inputVarName(i)
      // Example: bool_t in1Comp3, in2Comp3, in3Comp3;
      // TODO: set default value ?
      Some(s"$tpe ${inputs.mkString(", ")}; // $this")
    case _ => None
  }

  override def getLoopableCode = out.isConnected match {
    case true =>
      val result: ListBuffer[String] = ListBuffer()

      // Compute the result from the global variables
      result += s"$tpe $outputVarName = ${out.getValue}; // $out"

      // Set the output value to connected components
      for (inPort ← ComponentManager.findConnections(out))
        result += inPort.setInputValue(s"$outputVarName") + "; // " + inPort

      Some(result.mkString("\n"))
    case _ => None
  }

  /* Code generation */

  private def createInputs() = {
    for (i <- 1 to nbrInput) {
      val inVar = inputVarName(i)
      val in = new InputPort[T](this) {
        override val description = s"input $i"

        // Use global variable
        override def setInputValue(s: String): String = s"$inVar = $s"
      }
      inputs += in
    }
  }

  private def inputVarName(index: Int) = s"in${index}Comp$getId" // Example: in1Comp2
}
