package hevs.androiduino.dsl.components

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.fundamentals._
import hevs.androiduino.dsl.utils.ComponentNotFound

import scala.collection.mutable
import scalax.collection.edge.Implicits._
import scalax.collection.edge.LDiEdge
import scalax.collection.mutable.Graph

/**
 * Object used to store all components declared in the code. These components are stored in a graph. This data
 * structure is useful to find any types of components, connected or not, to fin its direct successors, etc.
 */
object ComponentManager extends Logging {

  // This contains a (mutable) graph representation of the components
  // that can be skimmed later on
  val cpGraph: Graph[Component, LDiEdge] = Graph.empty[Component, LDiEdge]

  def createComponentId() = IdGenerator.newUniqueId

  /**
   * Insert a component in the graph. Each component has a unique ID. It cannot appears more than once in the graph.
   * If it is already in the graph,  it will not be added.
   * @param c the component to add as node in the graph
   */
  def registerComponent(c: Component) = {
    cpGraph += c // Add the component as a node to the graph
  }

  /**
   * Remove all components from the graph and clear all previous IDs.
   */
  def unregisterComponents() = {
    cpGraph.clear()
    IdGenerator.reset() // Restart id generation from 0
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
  def addWire(from: OutputPort[_], to: InputPort[_]) = {
    // Get components "from" and "to". These components must be in the graph, or an exception is thrown.
    val (cpFrom, cpTo) = (cp(from.getOwnerId), cp(to.getOwnerId))

    val w = new Wire(from, to)

    assert(from.isConnected, "From port not connected !")
    assert(to.isConnected, "To port not connected !")

    // Add the connection (wire) between these to ports
    val outer = (cpFrom ~+> cpTo)(w) // Component to component with input (right) as label
    cpGraph += outer

    // ~>   directed
    // ~+>  directed with label
    // ~+#> directed with key label
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
   * Find all connected inputs nodes. An input node is a node without direct predecessor. The node must be connected
   * with another node at least, or it will be ignored (node degree > 0).
   * @return list of hardware without direct predecessor (considered as an input)
   */
  def findConnectedInputHardware: Set[hw_implemented] = {
    val in = cpGraph.nodes.filter(c => c.diPredecessors.isEmpty && c.edges.size > 0)
    in.map(x => x.value.asInstanceOf[hw_implemented]).toSet
  }

  /**
   * Return unconnected ports of all components of the graph.
   * @return all unconnected ports of all components
   */
  def findUnconnectedPorts: Seq[Port[_]] = {
    val ncPorts = mutable.ListBuffer.empty[Port[_]]
    for (n <- cpGraph.nodes) {
      val cp = n.value.asInstanceOf[Component]
      ncPorts ++= cp.getUnconnectedPorts
    }
    ncPorts.toSeq
  }

  /**
   * Return all unconnected nodes of the graph (node degree = 0) in a Seq of `Component`.
   * @return all unconnected nodes
   */
  def findUnconnectedComponents: Set[Component] = {
    val nc = cpGraph.nodes filter (c => c.degree == 0)
    nc.map(x => x.value.asInstanceOf[Component]).toSet
  }

  // Return a list of `InputPort`s that are connected
  def findConnections(port: OutputPort[_]): Seq[InputPort[_]] = {
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

  def numberOfConnectedHardware() = cpGraph.nodes count (c => c.degree > 0)

  def numberOfUnconnectedHardware() = cpGraph.nodes count (c => c.degree == 0)

  // This a basically a Tuple2, but they cannot be override
  // Also used by the DotGenerator
  class Wire(val from: OutputPort[_], val to: InputPort[_]) {
    override def toString = "Wire: " + from + "~" + to
  }

  private object IdGenerator {
    private var id = 0

    def newUniqueId = {
      id += 1
      id
    }

    def reset() = id = 0
  }

}