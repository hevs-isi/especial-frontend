package hevs.especial.dsl.components.fundamentals

import hevs.especial.dsl.components.ComponentManager

/**
 * Base class for all components (blocks) used in a program. These components, with there connections,
 * are stored only once in the graph. Methods `equals` and `hashCode` are used for that (class equality). Each
 * component has a unique `ìd` and each port of it has also a unique ID (`newUniquePortId` function) inside the
 * component. When a component is created, it is automatically added to the graph,
 * which is managed by the `ComponentManager`.
 */
abstract class Component {

  private val id = ComponentManager.createComponentId() // Id of component (must be unique)

  private var nbrOfPorts = 0 // Used to generate a unique ID for each port

  def newUniquePortId = {
    nbrOfPorts += 1
    nbrOfPorts
  }

  def getId = id

  /**
   * Optional description of the component.
   */
  protected val description: String = ""

  ComponentManager.registerComponent(this)

  // List of outputs of the block, if any.
  // The default implementation of a Seq is a List.
  def getOutputs: Option[Seq[OutputPort[_]]]

  // List of inputs of the block, if any.
  // The default implementation of a Seq is a List.
  def getInputs: Option[Seq[InputPort[_]]]

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

  def getDescription = description

  def getFullDescriptor = {
    toString +
      "\n\tInputs:  " + getInputs.getOrElse("None") +
      "\n\tOutputs: " + getOutputs.getOrElse("None")
  }

  // Used by the graph library
  override def equals(other: Any) = other match {
    // A component ID must be unique
    case that: Component => that.id == this.id
    case _ => false
  }

  // Used by the graph library
  override def hashCode = id.##

  override def toString = s"Cmp[$id] '$getDescription'"
}

trait hw_implemented {

  // Code inserted in the global section to declare global variables, constants, etc.
  def getGlobalCode: Option[String] = None

  // Code inserted in for function definitions (pre-main)
  def getFunctionsDefinitions: Option[String] = None

  // Code inserted to init the component (executed once)
  def getInitCode: Option[String] = None

  def getBeginOfMainAfterInit: Option[String] = None

  // Code inserted in the main loop
  def getLoopableCode: Option[String] = None
}
