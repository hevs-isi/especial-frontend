package hevs.especial.dsl.components

import hevs.especial.dsl.components.ComponentManager.IdGenerator

/**
 * Base class for all components (blocks) used in a program. These components, with there connections,
 * are stored only once in the graph. Methods `equals` and `hashCode` are used for that (class equality). Each
 * component has a unique `ìd` and each port of it has also a unique ID (`newUniquePortId` function) inside the
 * component. When a component is created, it is automatically added to the graph,
 * which is managed by the `ComponentManager`.
 * By default, a components has no input and no output. `getOutputs` and
 * `getInputs` methods must be overriding.
 */
abstract class Component {

  /**
   * Name of the component class. Default is the class name.
   */
  val name: String = this.getClass.getSimpleName // Example: Component

  /**
   * Description of the component. Default is empty.
   */
  val description: String = ""

  // Id of component (must be unique)
  private val id = ComponentManager.nextComponentId()

  // Used to generate a unique ID for each port
  private val portIdGen = {
    val g = new IdGenerator
    g.reset()
    g
  }

  /**
   * Generate a unique ID for a component port.
   * @return a unique port id inside the component
   */
  def nextPortId = portIdGen.nextId

  private var init: Boolean = false

  /**
   * @see Component.hashCode
   * @return unique ID of the component
   */
  def getId = id

  /**
   * Helper function to create a unique variable name for a component.
   * @return unique variable name for a component
   */
  def getVarId = s"Cmp$id"

  ComponentManager.registerComponent(this)

  def isInitialized: Boolean = init

  def initialized() = init = true

  /**
   * Check if at least one port of this component is not connected.
   * @return true if one or more ports are not connected
   */
  def isConnected: Boolean = getUnconnectedPorts.isEmpty

  /**
   * Get the list of all unconnected ports of this component.
   * @return all unconnected ports (input or output)
   */
  def getUnconnectedPorts: Seq[Port[_]] = {
    val ins = getInputs.getOrElse(Nil)
    val outs = getOutputs.getOrElse(Nil)
    (ins ++ outs).filter(c => c.isNotConnected)
  }

  /**
   * Component outputs, if any.
   * @return outputs of the component
   */
  def getOutputs: Option[Seq[OutputPort[_]]]

  /**
   * Component inputs, if any.
   * @return inputs of the component
   */
  def getInputs: Option[Seq[InputPort[_]]]

  def getFullDescriptor = {
    toString + ": " + description +
      "\n\t- Inputs:  " + (if(getInputs.isDefined) getInputs.get.mkString(", ") else "None") +
      "\n\t- Outputs: " + (if(getOutputs.isDefined) getOutputs.get.mkString(", ") else "None") + "\n"
  }

  override def toString = s"Cmp[$id] '$name'"


  // Used by the graph library
  override def equals(other: Any) = other match {
    // A component ID must be unique
    case that: Component => that.id == this.id
    case _ => false
  }

  // Used by the graph library
  override def hashCode = id.##
}


trait hw_implemented {

  /**
   * Include (header) files on the top of the generated code.
   * @return the list of path/file name to include with its extension
   */
  def getIncludeCode: Seq[String] = Nil

  // Code inserted in the global section to declare global variables, constants, etc.
  def getGlobalCode: Option[String] = None

  // Code inserted in for function definitions (pre-main)
  def getFunctionsDefinitions: Option[String] = None

  // Code inserted to init the component (executed once)
  def getInitCode: Option[String] = None

  def getBeginOfMainAfterInit: Option[String] = None

  // Code inserted in the main loop
  def getLoopableCode: Option[String] = None

  // Final code after the main loop
  def getExitCode: Option[String] = None
}
