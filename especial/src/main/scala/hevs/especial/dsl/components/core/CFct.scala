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

    override def setInputValue(s: String) = CFct.this.setInputValue(s)
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = Some(Seq(in))

  /* Code generation */

  // These methods cannot be overridden. The user can only add some code in the while loop.

  override final def getIncludeCode = Nil

  override final def getGlobalCode = {
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
    val customLoopCode = "// -- User input code " + loopCode + "// --"

    // Each component has to propagate its output value to all connected components
    val in = ComponentManager.findConnections(out)
    val results = ListBuffer.empty[String]
    for (inPort <- in)
      results += inPort.setInputValue(out.getValue) + "; // " + inPort
    Some(customLoopCode + "\n" + results.mkString("\n"))

  }

  /* Custom implementation */

  /**
   * generate the C code to read the value of the output port.
   * @return the C code to read the output value
   */
  def getOutputValue: String

  /**
   * Function used to set the input value of the input port.
   * @param s the name of the variable or C code to set as input
   * @return the C code generated to update the input of the port
   */
  def setInputValue(s: String): String

  /**
   * Declare and initialize some global variables if necessary.
   * Each variable to declare globally must have a name (as a String) and an initial value.
   *
   * @return list of global variables to declare and initialize
   */
  def globalVars: Map[String, CType]

  /**
   * Custom component code to execute on each loop ticks.
   * @return the code to execute on each loop
   */
  def loopCode: String
}
