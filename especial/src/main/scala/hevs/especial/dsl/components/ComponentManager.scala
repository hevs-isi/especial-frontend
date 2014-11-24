package hevs.especial.dsl.components

import grizzled.slf4j.Logging
import hevs.especial.utils.ComponentNotFound

import scala.collection.mutable
import scalax.collection.edge.Implicits._
import scalax.collection.edge.LDiEdge
import scalax.collection.mutable.Graph

/**
 * Object used to store all components declared in the code. These components are stored in a graph. This data
 * structure is useful to find any types of components, connected or not, to fin its direct successors, etc.
 */
object ComponentManager extends Logging {

  /** Mutable graph representation of all the components of the program. */
  val cpGraph: Graph[Component, LDiEdge] = Graph.empty[Component, LDiEdge]

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
   * Insert a component in the graph. Each component has a unique ID. It cannot appears more than once in the graph.
   * If it is already in the graph,  it will not be added.
   * @param c the component to add as node in the graph
   */
  def registerComponent(c: Component): Unit = {
    cpGraph += c // Add the component as a node to the graph
  }

  /**
   * Remove all components from the graph and clear all previous IDs.
   */
  def reset(): Unit = {
    cpGraph.clear()
    cmpIdGen.reset() // Restart id generation from 0
  }

  /**
   * Add a connection between two `Port`s.
   * 1) Owners of port must be in the graph
   * 2) The input must be unconnected
   *
   * @param from port from
   * @param to port to
   * @return
   */
  def addWire(from: OutputPort[CType], to: InputPort[CType]): Unit = {
    // Get components "from" and "to". These components must be in the graph, or an exception is thrown.
    val (cpFrom, cpTo) = (cp(from.getOwnerId), cp(to.getOwnerId))

    val w = new Wire(from, to) // Add a wire between the two ports

    assert(from.isConnected, "From port not connected !")
    assert(to.isConnected, "To port not connected !")

    // Add the connection (wire) between these to ports.
    // The edge is directed with a key label. The label must be a key because an output can be connected to multiple
    // inputs. It must be possible to add multiple wire from an to the same nodes, with different labels.
    val outer = (cpFrom ~+#> cpTo)(w)
    cpGraph += outer
  }

  /**
   * Get a Component from a node graph by its id.
   * @see cp
   * @param cpId the component id to search for
   * @return the component node or an exception if not found
   */
  private def cp(cpId: Int): Component = {
    getNode(cpId).value.asInstanceOf[Component]
  }

  /**
   * Search for a node in the graph by a component ID. If the component is not
   * found, an exception is thrown. The component id is unique. At most one component can be found.
   * @param cpId the component id to search for
   * @return the graph node (with the Component as value)
   */
  def getNode(cpId: Int): cpGraph.NodeT = {
    cpGraph.nodes find (c => c.value.asInstanceOf[Component].getId == cpId) match {
      case Some(c) => c
      case None =>
        // Fatal exception: must be in the graph
        throw new ComponentNotFound(s"Component id $cpId not found !")
    }
  }

  /**
   * Return unconnected ports of all components of the graph.
   * @return all unconnected ports of all components
   */
  def findUnconnectedPorts: Seq[Port[CType]] = {
    val ncPorts = mutable.ListBuffer.empty[Port[CType]]
    for (n <- cpGraph.nodes) {
      val cp = n.value.asInstanceOf[Component]
      ncPorts ++= cp.getUnconnectedPorts
    }
    ncPorts.toSeq
  }

  def numberOfConnectedHardware() = cpGraph.nodes.size - numberOfUnconnectedHardware()

  /**
   * @see findUnconnectedComponents
   */
  def numberOfUnconnectedHardware() = findUnconnectedComponents.size

  /**
   * Return all unconnected nodes of the graph.
   * A component is considered as unconnected if it has at least one input or output and no connections to other
   * components.
   * A component without input and output is considered as connected.
   *
   * @see findConnectedInputHardware
   * @return all unconnected nodes (with at least one input or output)
   */
  def findUnconnectedComponents: Set[Component] = {
    val nc = cpGraph.nodes filter { c =>
      val cp = c.value.asInstanceOf[Component]
      // If no I/O, not considered has unconnected
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

      if (input) // Input = no direct predecessors
        c.diPredecessors.isEmpty && c.edges.size > 0 || io.size == 0
      else // Output = no direct successors
        c.diSuccessors.isEmpty && c.edges.size > 0 || io.size == 0
    }
    // Return the node value as a Component
    ret.map(x => x.value.asInstanceOf[Component]).toSet
  }

  // Return a list of `InputPort`s that are connected
  def findConnections(port: OutputPort[CType]): Seq[InputPort[CType]] = {
    val cpFrom = cpGraph.nodes find (c => c.value.asInstanceOf[Component].equals(port.getOwner))
    val edges = cpFrom.get.edges // all connections of this component (from and to components)

    // Find all connections (wires/edges) of this component to OTHER components. This test must be included because
    // it is not a filter on successors but on edges (because we need the label of the edge).
    val connections = edges filter {
      w => w.label.asInstanceOf[Wire].from == port && // Filter the OutputPort id
        w.label.asInstanceOf[Wire].to.getOwnerId != port.getOwnerId // Must be another component
    }
    // Return only the InputPort of the connected component, extracted from the label of the edge
    val tos = connections.toSeq.map(x => x.label.asInstanceOf[Wire].to)
    tos
  }

  // This a basically a Tuple2, but they cannot be override
  // Also used by the DotGenerator
  class Wire(val from: OutputPort[CType], val to: InputPort[CType]) {

    override def equals(other: Any) = other match {
      case that: Wire => that.from == from && that.to == to
      case _ => false
    }

    override def hashCode = 41 * from.hashCode() + to.hashCode()

    override def toString = "Wire: " + from + "~" + to
  }

  /**
   * Helper class used to generate a unique ID.
   *
   * Each component stored in the graph has a unique ID. This is necessary to equals nodes in the graph.
   * Each port of a component has also a unique ID. Used to equal ports and find connections (wires).
   */
  class IdGenerator {
    private var id: Int = 0

    def nextId = {
      val currId = id
      id += 1
      currId
    }

    def reset(): Unit = id = 0
  }
}