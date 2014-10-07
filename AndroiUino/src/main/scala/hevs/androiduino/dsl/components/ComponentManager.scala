package hevs.androiduino.dsl.components

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.fundamentals.{Component, InputPort, OutputPort, hw_implemented}
import hevs.androiduino.dsl.utils.ComponentNotFound

import scalax.collection.edge.Implicits._
import scalax.collection.edge.LDiEdge
import scalax.collection.mutable.Graph

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
   * Find all connected inputs nodes. An input node is a node without direct predecessor.
   * @return list of hardware without direct predecessor (considered as an input)
   */
  def findConnectedInputHardware: Set[hw_implemented] = {
    val in = cpGraph.nodes.filter(c => c.diPredecessors.isEmpty && c.edges.size > 0)
    in.map(x => x.value.asInstanceOf[hw_implemented]).toSet
  }

  /**
   * Return all unconnected nodes of the graph in a Seq of `Component`.
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

  // TODO beautify this (factorize it)
  def generateInitCode() = {
    var result = ""

    for (c ← cpGraph.nodes.toList) {
      // In the graph we have nodes. Each node has a content which is a component
      val comp = c.value.asInstanceOf[Component]
      assert(comp.isInstanceOf[Component])
      if (comp.isInstanceOf[hw_implemented]) {
        val hw_c = comp.asInstanceOf[hw_implemented]

        if (hw_c.getInitCode.isDefined)
          result += hw_c.getInitCode.get + "\n"
      }
    }
    result
  }

  def generateBeginMainCode() = {
    var result = ""

    for (c ← cpGraph.nodes.toList) {
      val comp = c.value
      assert(comp.isInstanceOf[Component])

      if (comp.isInstanceOf[hw_implemented]) {
        val hw_c = comp.asInstanceOf[hw_implemented]

        if (hw_c.getBeginOfMainAfterInit.isDefined)
          result += hw_c.getBeginOfMainAfterInit.get + "\n"
      }
    }
    result
  }

  def generateGlobalCode() = {
    val cp = findConnectedHardware // Connected nodes only

    var result = "//*// generateGlobalCode\n"
    for (c <- cp if c.getGlobalCode.isDefined) {
      result += c.getGlobalCode.get + "\n"
    }
    result + "\n"
  }

  /**
   * Return all connected nodes of the graph in a Seq of `hw_implemented`.
   * All nodes with a degree of o are ignored.
   * @return all connected nodes
   */
  private def findConnectedHardware: Set[hw_implemented] = {
    // TODO encore utile ou pas ?
    // TODO utiliser le resolver
    val nc = cpGraph.nodes filter (c => c.degree > 0)
    nc.map(x => x.value.asInstanceOf[hw_implemented]).toSet
  }

  def numberOfConnectedHardware() = cpGraph.nodes count (c => c.degree > 0)

  def numberOfUnconnectedHardware() = cpGraph.nodes count (c => c.degree == 0)

  def generateFunctionsCode() = {
    // Works but not really clearer
    //		val filteredInstances : List[hw_implemented] = comps.filter(_.isInstanceOf[hw_implemented]).map(_.asInstanceOf[hw_implemented])
    //		val functionCode = filteredInstances.map(x => x.getFunctionsDefinitions()).foldLeft("")(_ + _)

    var result = "//*// generateFunctionsCode\n"
    for (c ← cpGraph.nodes.toList) {
      val comp = c.value
      assert(comp.isInstanceOf[Component])

      if (comp.isInstanceOf[hw_implemented]) {
        val hw_c = comp.asInstanceOf[hw_implemented]

        if (hw_c.getFunctionsDefinitions.isDefined)
          result += hw_c.getFunctionsDefinitions.get + "\n\n"
      }
    }

    result
  }

  // TODO beautify all these methods with pattern matching
  //TODO Move all generate code to others class
  //TODO Differents generators with pattern matching on trait to now if it is harware or simulated ?

  def generateLoopingCode() = {
    var result = "//*// generateLoopingCode\n"
    for (c ← cpGraph.nodes.toList) {
      val comp = c.value
      assert(comp.isInstanceOf[Component])

      if (comp.isInstanceOf[hw_implemented]) {
        val hw_c = comp.asInstanceOf[hw_implemented]

        if (hw_c.getLoopableCode.isDefined)
          result += hw_c.getLoopableCode.get + "\n"
      }
    }
    result
  }

  // This a basically a Tuple2, but they cannot be override
  // Also used by the DotGenerator
  class Wire(val from: OutputPort[_], val to: InputPort[_]) {
    override def toString = "MyWire " + from + "~" + to
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