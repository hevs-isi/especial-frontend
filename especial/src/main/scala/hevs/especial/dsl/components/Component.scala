package hevs.especial.dsl.components

import hevs.especial.dsl.components.ComponentManager.IdGenerator

import scala.reflect.runtime.universe._

/**
 * Base class for all components (blocks) used in a program.
 *
 * These components, with there connections, are stored only once in the graph. Methods `equals` and `hashCode` are used
 * for that (class equality). Each component has a unique `ìd` and each port of it has also a unique ID
 * (`newUniquePortId` function) inside the component. When a component is created, it is automatically added to the
 * graph, which is managed by the [[ComponentManager]]. By default, a components has no input and no output.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
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

  // A component is create with a unique ID.
  // All components are automatically added to the graph once instantiated.
  ComponentManager.addComponent(this)

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


  // Helper method to get the type of the template class.
  // Used to declare the global C variable with the correct type.
  protected def getTypeString[T <: CType : TypeTag]: String = {
    val clazz: Class[_] = getTypeClass
    CType.t.get(clazz).get // Must exist in the map
  }

  protected def getTypeClass[T <: CType : TypeTag]: Class[_] = {
    val mirror = runtimeMirror(getClass.getClassLoader)
    mirror.runtimeClass(typeOf[T].typeSymbol.asClass)
    // clazz.newInstance().asInstanceOf[T] // If instance is necessary
  }

  /* Component variables names */

  /**
   * Input variable name if more than one. Add the index as suffix.
   * @see valName
   */
  private[components] def inValName(index: Int) = valName("in" + String.valueOf(index + 1))

  /** @see valName */
  private[components] def inValName() = valName("in")

  /**
   * Output variable name if more than one. Add the index as suffix.
   * @see valName
   */
  private[components] def outValName(index: Int) = valName("out" + String.valueOf(index + 1))

  /** @see valName */
  def outValName() = valName("out")

  /**
   * Create a custom and unique variable name.
   * Used by the component in the generated code.
   * @param prefix name of the variable (used as prefix)
   * @return variable name with the component number (example prefix_cmp02)
   */
  def valName(prefix: String) = {
    val cmpId = f"$id%02d"
    s"${prefix}_cmp$cmpId" // Example: prefix_cmp02
  }

  /**
   * Check if at least one port of the component is not connected.
   * Used by the `DotGenerator` to draw unconnected components with a different color.
   *
   * @return true if one or more ports are not connected
   */
  def isConnected: Boolean = getUnconnectedPorts.isEmpty

  /**
   * Get the list of all unconnected I/O ports of this component.
   * @return all unconnected ports (input or output)
   */
  def getUnconnectedPorts: Seq[Port[CType]] = {
    val ins = getInputs.getOrElse(Nil)
    val outs = getOutputs.getOrElse(Nil)
    (ins ++ outs).filter(port => port.isNotConnected)
  }

  /**
   * Component outputs ports, if any.
   * @return outputs of the component
   */
  def getOutputs: Option[Seq[OutputPort[CType]]]

  /**
   * Component inputs ports, if any.
   * @return inputs of the component
   */
  def getInputs: Option[Seq[InputPort[CType]]]

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

  /**
   * Equals components.
   * Each component are identified by a unique generated ID. Used by the graph library.
   *
   * @param other object to equals
   * @return `true` if the component ID is the same, `false` otherwise
   */
  override def equals(other: Any) = other match {
    // A component ID must be unique
    case that: Component => that.id == this.id
    case _ => false
  }
  
  override def hashCode = id.## // Used by the graph library
}
