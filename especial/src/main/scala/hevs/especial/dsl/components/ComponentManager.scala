package hevs.especial.dsl.components

import java.io.IOException

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.core.Constant
import hevs.especial.utils.{ComponentNotFound, CycleException, IoTypeMismatch}

import scala.language.higherKinds
import scalax.collection.GraphPredef._
import scalax.collection.constrained.mutable.Graph
import scalax.collection.constrained.{Config, ConstraintCompanion}
import scalax.collection.edge.LDiEdge

/**
 * Object used to store all components declared in the code.
 *
 * Components are stored in single and shared graph.
 * This data structure is useful to find any types of components, connected or not, to fin its direct successors, etc.
 *
 * Each component is identified by a unique generated ID (see [[hevs.especial.dsl.components.ComponentManager.IdGenerator]].
 * It can be stored only once in the graph. A [[Wire]] is used as an edge label to store a connection composed by two
 * components ports (an [[OutputPort]] source and an [[InputPort]] destination).
 *
 * The component graph is a directed acyclic graph (`DAG`). A dynamic constraint is used to prevent addition of
 * cyclic nodes or edges to the graph.
 *
 * @version 2.1
 * @author Christopher Metrailler (mei@hevs.ch)
 */
object ComponentManager extends Logging {

  /* Dynamic acyclic constraint definition for the graph. */
  object AcyclicWithException {

    import scalax.collection.constrained.Graph
    import scalax.collection.constrained.constraints.{Acyclic => AcyclicBase}

    object Acyclic extends ConstraintCompanion[AcyclicBase] {

      @throws[CycleException]("If a cycle is found (graph constraint to be a DAG).")
      def apply[N, E[X] <: EdgeLikeIn[X]](self: Graph[N, E]) =
        new AcyclicBase[N, E](self) {
          override def onAdditionRefused(refusedNodes: Iterable[N],
                                         refusedEdges: Iterable[E[N]],
                                         graph: Graph[N, E]) = {
            // Throw an exception if the node or edges cannot be added to the graph.
            val label = refusedEdges.head.label.asInstanceOf[Wire]
            throw CycleException(label)
          }
        }
    }

  }

  import hevs.especial.dsl.components.ComponentManager.AcyclicWithException._

  /** Constraint of the graph. The graph must be acyclic and unconnected nodes are allowed. */
  implicit val config: Config = Acyclic // && Connected

  /** Mutable graph representation of all the components of the program. */
  protected val cpGraph: Graph[Component, LDiEdge] = Graph.empty[Component, LDiEdge]


  // Used to generate a unique ID for each component
  private val cmpIdGen: IdGenerator = {
    val g = new IdGenerator()
    g.reset()
    g
  }

  /**
   * Create a unique component id to store in the graph.
   * @return a unique component id
   */
  def nextComponentId() = cmpIdGen.nextId

  /**
   * Insert a component in the graph.
   *
   * Each component has a unique ID and can be only once in the graph.
   *
   * If the component already exist in the graph, then the existing node is returned.
   * This works only if `equals` and `hashcode` functions of the components are implemented.
   *
   * If the component already exist, but with another type, an exception is thrown. This is the case when an I/O is
   * used twice on the same pin, with different functions (PwmOutput and DigitalOutput for instance).
   *
   * @param node the component to add in the graph (as node)
   * @return `None` if the component has been added successfully, or the existing instance if already in the graph
   */
  @throws[CycleException]("If a cycle is found in the graph (not a DAG).")
  @throws[IoTypeMismatch]("If an I/O is already configured with another type.")
  @throws[IOException]("If the node cannot be added into the graph.")
  def addComponent(node: Component): Option[node.type] = {
    // Try to add the component as a node to the graph. Not added if it exist already.
    // Components ports must be connected manually.
    val extNodes = cpGraph.nodes.filter(p => p.value == node)
    if (extNodes.size > 0) {
      logger.trace(s"Component $node already exist in the graph.")

      // If the component already exist, check if it has the same function (class) as the current component.
      // If not (example: PwmOutput and DigitalOutput), an exception is thrown. See issue #14.
      val cmp = extNodes.head.value
      if (cmp.getClass != node.getClass) {
        val extCmp = cmp.asInstanceOf[Component]
        throw IoTypeMismatch(extCmp, node) // Already used with another type
      }
      else {
        // Return the existing component, directly with the correct type.
        // The type conversion is safe.
        Some(cpGraph.get(node).value.asInstanceOf[node.type])
      }
    }
    else {
      cpGraph.add(node) match {
        case true =>
          logger.trace(s"Component $node added to the graph.")
          None
        case _ => throw new IOException("Unable to add the component to the graph !")
      }
    }
  }

  /**
   * Remove the component of the graph.
   * All edges of the node (from/to the node) are removed automatically. Connected ports with the component are
   * disconnected.
   *
   * @param cpId the ID of the component to remove
   * @return `true` if successfully removed, `false` otherwise
   */
  def removeComponent(cpId: Int): Boolean = {
    // Before removing the component, ports must be disconnected manually
    val nOpt = cpGraph.nodes.find(n => n.value.asInstanceOf[Component].getId == cpId)

    if (nOpt.isEmpty)
      return false // Node not found

    // Disconnected all ports connected with the component to remove
    val node = nOpt.get
    for (e <- node.edges) {
      val label: Wire = e.label.asInstanceOf[Wire]
      label.from.disconnect() // Disconnect the output "from"
      label.to.disconnect() // Also disconnect the input "to" (this component)
    }

    // Finally remove the component and its edges
    cpGraph.remove(node)
  }

  /**
   * Order of the graph.
   * @return the number of nodes in the graph (order of the graph)
   */
  def numberOfNodes = cpGraph.order

  /**
   * @return the number of edges
   */
  def numberOfEdges = cpGraph.edges.size

  def getDotGraph = cpGraph // Used by the `dot` generator

  /**
   * Remove all components from the graph and clear all previous IDs.
   */
  def reset(): Unit = {
    cpGraph.clear()
    cmpIdGen.reset() // Restart id generation from 0
  }

  /**
   * Add a connection between two `Port`s.
   *
   * 1) Owners of port must be in the graph
   * 2) The input must be unconnected
   *
   * @param from port from
   * @param to port to
   * @return
   */
  def addWire[T <: CType](from: OutputPort[T], to: InputPort[T]): Unit = {
    // Get components "from" and "to". These components must be in the graph, or an exception is thrown.
    val (cpFrom, cpTo) = (cp(from.getOwnerId), cp(to.getOwnerId))

    val w = new Wire(from, to) // Add a wire between the two ports

    assert(from.isConnected, "From port not connected !")
    assert(to.isConnected, "To port not connected !")

    // Add the connection (wire) between these to ports.
    // The edge is directed with a key label. The label must be a key because an output can be connected to multiple
    // inputs. It must be possible to add multiple wire from an to the same nodes, with different labels.
    import scalax.collection.edge.Implicits._
    val outer = (cpFrom ~+#> cpTo)(w)
    cpGraph += outer
  }

  /**
   * Get a Component from a node graph by its id.
   * @see getNode
   * @param cpId the component id to search for
   * @return the component node or an exception if not found
   */
  @throws[ComponentNotFound]("If the component is not in the graph.")
  private def cp(cpId: Int): Component = {
    getNode(cpId).value.asInstanceOf[Component]
  }

  /**
   * Search a node in the graph by ID.
   * Return the node in the graph with the corresponding ID. An exception is thrown in the component was not found.
   * All nodes of the graph have a unique id. Only one unique component can be returned.
   *
   * @param cpId the component id to search for
   * @return the component as a graph node (`Component` as value, with edges)
   */
  @throws[ComponentNotFound]("If the component is not in the graph.")
  def getNode(cpId: Int): cpGraph.NodeT = {
    cpGraph.nodes find (c => c.value.asInstanceOf[Component].getId == cpId) match {
      case Some(c) => c
      case None =>
        // Fatal exception: must be in the graph
        throw ComponentNotFound(cpId)
    }
  }

  /**
   * Return all nodes of the graph as [[Component]]s.
   * @return all graph nodes as a [[Set]] of [[Component]]
   */
  def getComponents: Set[Component] = {
    cpGraph.nodes.map(node => node.value.asInstanceOf[Component]).toSet
  }

  /**
   * @return the number of connected nodes
   */
  def numberOfConnectedHardware() = cpGraph.nodes.size - numberOfUnconnectedHardware()

  /**
   * @see findUnconnectedComponents
   */
  def numberOfUnconnectedHardware() = findUnconnectedComponents.size

  /**
   * Return all unconnected nodes of the graph.
   * A component is considered as unconnected if it has at least one input or output and no connections to other
   * components.
   * A component without input and output (total of 0 I/O) is considered as connected and its code will be generated.
   *
   * @see findConnectedInputHardware
   * @return all unconnected nodes (with at least one input or output)
   */
  def findUnconnectedComponents: Set[Component] = {
    val nc = cpGraph.nodes filter { c =>
      val cp = c.value.asInstanceOf[Component]
      // If no I/O, NOT considered has unconnected
      val io = cp.getInputs.getOrElse(Nil) ++ cp.getOutputs.getOrElse(Nil)
      c.degree == 0 && io.length != 0
    }
    nc.map(x => x.value.asInstanceOf[Component]).toSet
  }

  /**
   * Find all connected inputs nodes. An input node is a node without direct predecessor. To be considered as
   * connected, a node must have at least one input or output and connected with at least one other node.
   * A component without input and output is considered as connected.
   *
   * @see findUnconnectedComponents
   * @return list of connected inputs
   */
  def findConnectedInputHardware: Set[Component] = findConnectedIOHardware(input = true)

  /**
   * Find all connected outputs nodes. An output node is a node without direct successors. To be considered as
   * connected, a node must have at least one input or output and connected with at least one other node.
   * A component without input and output is considered as connected.
   *
   * @see findUnconnectedComponents
   * @return list of connected outputs
   */
  def findConnectedOutputHardware: Set[Component] = findConnectedIOHardware(input = false)

  // Find connected input or output in the graph
  private def findConnectedIOHardware(input: Boolean) = {
    val ret = cpGraph.nodes.filter { c =>
      val cp = c.value.asInstanceOf[Component]
      val io = cp.getInputs.getOrElse(Nil) ++ cp.getOutputs.getOrElse(Nil)

      if (input) // Input = no direct predecessors or '0' I/O
        c.diPredecessors.isEmpty && c.edges.size > 0 || io.size == 0
      else // Output = no direct successors
        c.diSuccessors.isEmpty && c.edges.size > 0
    }
    // Return the node value as a Component
    ret.map(x => x.value.asInstanceOf[Component]).toSet
  }

  /**
   * Get the [[OutputPort]] connected with the specified [[InputPort]].
   *
   * If the input is not connected, the constant value '0' is returned as dummy value.
   *
   * @version 2.0
   * @param port the port to search is input
   * @return the [[OutputPort]] connected with the input port
   */
  def findPredecessorOutputPort(port: InputPort[CType]): OutputPort[CType] = {
    val cp = cpGraph.nodes find (c => c.value.asInstanceOf[Component].equals(port.getOwner))
    val edges = cp.get.edges // all connections of this component (from and to components)

    // Search the corresponding wire. Should be only one.
    val connections = edges filter {
      w => w.label.asInstanceOf[Wire].to == port &&
        w.label.asInstanceOf[Wire].from.getOwnerId != port.getOwnerId
    }

    if (connections.size == 0) {
      // Port not found. The input is NOT connected...
      val cst = Constant[uint8](uint8(0))
      cst.out // Return the constant value '0' as dummy value.
    }
    else
      connections.head.label.asInstanceOf[Wire].from // Return the connected port
  }

  /**
   * Helper class used to generate a unique ID.
   *
   * Each component stored in the graph has a unique ID. This is necessary to equals nodes in the graph.
   * Each port of a component has also a unique ID. Used to equal ports and find connections (wires).
   */
  private[components] class IdGenerator {
    private var id: Int = 0

    /**
     * Generate a new unique ID for component and ports.
     * @return a new unique id
     */
    def nextId = {
      val currId = id
      id += 1
      currId
    }

    /**
     * Reset the generator. Next id will be '0'.
     */
    def reset(): Unit = id = 0
  }

}