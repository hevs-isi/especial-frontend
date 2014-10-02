package hevs.androiduino.dsl.components.fundamentals

import hevs.androiduino.dsl.components.{ComponentManager, IdGenerator}
import scala.reflect.runtime.universe._

// TODO: un composant est sotcké dans un graph, doit avoir `override def equals` et `override def hashCode`
abstract class Component {

  private val id = IdGenerator.newUniqueId // Id component (must be unique)

  private var nbrOfPorts = 0 // Used to generate a unique ID for each port

  def newUniquePortId = {
    nbrOfPorts += 1
    nbrOfPorts
  }

  def getId = id

  // Component description (optional)
  protected val description: String = ""

  ComponentManager.registerComponent(this)

  // List of outputs of the block, if any.
  // The default implementation of a Seq is a List.
  def getOutputs: Option[Seq[OutputPort[_]]]

  // List of inputs of the block, if any.
  // The default implementation of a Seq is a List.
  def getInputs: Option[Seq[InputPort[_]]]

  def getDescription = description

  def getFullDescriptor = {
    toString +
      "\n\tInputs:  " + getInputs.getOrElse("None") +
      "\n\tOutputs: " + getOutputs.getOrElse("None")
  }

  // For the graph
  override def equals(other: Any) = other match {
    // A component ID must be unique
    case that: Component => that.id == this.id
    case _ => false
  }

  // For the graph
  override def hashCode = id.##

  override def toString = s"Cmp[$id] '$getDescription'"
}

trait hw_implemented {
  // Code inserted to init the component (executed
  // once in C++)
  def getInitCode: Option[String] = None

  def getBeginOfMainAfterInit: Option[String] = None

  // Code inserted in the main loop of the C++
  def getLoopableCode: Option[String] = None

  // Code inserted in the global section of the C++
  def getGlobalConstants: Option[String] = None

  // Code inserted in for function definitions (pre-main)
  def getFunctionsDefinitions: Option[String] = None

}

//abstract class Button(description: String) extends Component(description)
//class SW_Button(pin: Int) extends Button("A software button")
//class HW_Button(pin: Int) extends Button("An hardware button")

