package hevs.especial.dsl.components

import hevs.especial.dsl.components.ComponentManager.IdGenerator

/**
 * Base class for all components (blocks) used in a program. These components, with there connections,
 * are stored only once in the graph. Methods `equals` and `hashCode` are used for that (class equality). Each
 * component has a unique `ìd` and each port of it has also a unique ID (`newUniquePortId` function) inside the
 * component. When a component is created, it is automatically added to the graph,
 * which is managed by the `ComponentManager`.
 * By default, a components has no input and no output. `getOutputs` and `getInputs` methods must be overriding.
 */
abstract class Component {

  /** Name of the component class. Default is the class name. */
  val name: String = this.getClass.getSimpleName // Example: Component

  /** Description of the component. Default is empty. */
  val description: String = ""

  // Id of component (must be unique)
  private val id = ComponentManager.nextComponentId()

  // Used to generate a unique ID for each port
  private val portIdGen = {
    val g = new IdGenerator
    g.reset()
    g
  }

  // Once the component is instantiated, a unique ID is generated
  // and it is registered automatically to the graph.
  ComponentManager.registerComponent(this)

  /**
   * Generate a unique ID for a component port.
   * @return a unique port id inside the component
   */
  private[components] def nextPortId = portIdGen.nextId

  /**
   * @see Component.hashCode
   * @return unique ID of the component
   */
  def getId = id

  /**
   * Helper function to create a unique variable name for a component.
   * @return unique variable name for a component
   */
  private[components] def getVarId = s"Cmp$id"

  /**
   * Check if at least one port of this component is not connected.
   * @return true if one or more ports are not connected
   */
  def isConnected: Boolean = getUnconnectedPorts.isEmpty

  /**
   * Get the list of all unconnected ports of this component.
   * @return all unconnected ports (input or output)
   */
  private[components] def getUnconnectedPorts: Seq[Port[_]] = {
    val ins = getInputs.getOrElse(Nil)
    val outs = getOutputs.getOrElse(Nil)
    (ins ++ outs).filter(c => c.isNotConnected)
  }

  /**
   * Component outputs ports, if any.
   * @return outputs of the component
   */
  def getOutputs: Option[Seq[OutputPort[_]]]

  /**
   * Component inputs ports, if any.
   * @return inputs of the component
   */
  def getInputs: Option[Seq[InputPort[_]]]

  /**
   * Print a description of the component with the list of input and output ports.
   * @return the component description with I/O ports
   */
  def getFullDescriptor = {
    toString + ": " + description +
      "\n\t- Inputs:  " + (if (getInputs.isDefined) getInputs.get.mkString(", ") else "None") +
      "\n\t- Outputs: " + (if (getOutputs.isDefined) getOutputs.get.mkString(", ") else "None") + "\n"
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
