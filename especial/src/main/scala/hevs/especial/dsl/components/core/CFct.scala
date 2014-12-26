package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Helper class used to create simple component with one input and one output using a small piece of C code.
 *
 * This helper class can be used to code simple component using a small C code. The component must have one input and
 * one output. I/O type can be defined. The user can declare some global variables if necessary, and add some C code
 * in the while loop. All other function are not accessible.
 *
 * The output value must be updated in the loop code. Then, it is automatically propagated to all connected
 * components. The C loop code must be none blocking and as fast as possible to not block the code execution. The C
 * code is not checked and must be valid.
 *
 * Typical application are simple logic circuit.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @tparam I input type
 * @tparam O output type
 */
abstract class CFct[I <: CType : TypeTag, O <: CType : TypeTag]() extends Component
with In1 with Out1 with HwImplemented {

  override val description = s"custom C block"

  /* I/O management */

  val out = new OutputPort[O](this) {
    override val name = s"out"
    override val description = "custom output"

    override def getValue: String = CFct.this.getOutputValue
  }

  val in = new InputPort[I](this) {
    override val name = "in"
    override val description = "custom input"
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = Some(Seq(in))

  /* Code generation */

  // These methods cannot be overridden. The user can only add some code in the while loop.

  override final def getIncludeCode = Nil

  override final def getGlobalCode: Option[String] = {
    if(globalVars.isEmpty)
      return None // No global definition

    // Global variables declaration
    val out = ListBuffer.empty[String]
    for (v <- globalVars) {
      val sType = v._2.getType
      out += s"$sType ${v._1} = ${v._2.v};"
    }
    Some(out.mkString("\n"))
  }

  override final def getFunctionsDefinitions = None

  override final def getInitCode = None

  override final def getBeginOfMainAfterInit = None

  override final def getExitCode = None

  override final def getLoopableCode = {
    // The custom component code to paste in the loop
    val customLoopCode = s"\n// -- User input code of `$name` $loopCode // --\n"
    Some(customLoopCode)
  }

  /* Custom implementation */

  /**
   * Generate the C code to read the value of the output port.
   * @return the C code to read the output value
   */
  def getOutputValue: String

  /**
   * Read the value of the input port of the component.
   * @return the value of the input (C code as a String)
   */
  def getInputValue: String = {
    ComponentManager.findPredecessorOutputPort(in).getValue
  }

  /**
   * Declare and initialize some global variables if necessary.
   * Each variable to declare globally must have a name (as a String) and an initial value.
   *
   * @return list of global variables to declare and initialize
   */
  val globalVars: Map[String, CType] = Map.empty

  /**
   * Custom component code to execute on each loop ticks.
   * @return the C code to execute on each loop, as String
   */
  def loopCode: String
}
