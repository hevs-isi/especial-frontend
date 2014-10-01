package hevs.androiduino.dsl.components

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.fundamentals.{hw_implemented, InputPort, OutputPort, Component}
import hevs.androiduino.dsl.utils.ComponentNotFound

import scalax.collection.edge.Implicits._
import scalax.collection.edge.LDiEdge
import scalax.collection.mutable.Graph

object IdGenerator {
  private var id = 0

  def newUniqueId = {
    id += 1
    id
  }
}

object ComponentManager extends Logging {

  // This contains a (mutable) graph representation of the components
  // that can be skimmed later on
  val cpGraph: Graph[Component, LDiEdge] = Graph.empty[Component, LDiEdge]

  /**
   * Insert a component in the graph. Each component has a unique ID. It cannot appears more than once in the graph.
   * If it is already in the graph,  it will not be added.
   * @param c the component to add as node in the graph
   */
  def registerComponent(c: Component) = {
    cpGraph += c // Add the component as a node to the graph
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
   * Search for a component the graph nodes by id. If the component is not found, an exception is thrown.
   * @param cpId the component id to search
   * @return the component node or an exception if not found
   */
  private def cp(cpId: Int): Component = {
    // Find a component by ID. Exist once only.
    val cp = cpGraph.nodes find (c => c.value.asInstanceOf[Component].getId == cpId)
    cp match {
      case Some(c) => c
      case None => throw ComponentNotFound(cpId)
    }
  }

  def findUnconnectedComponents: Seq[Component] = {
    val nc = cpGraph.nodes filter (c => c.degree == 0)
    // Return a seq of components
    nc.map(x => x.value.asInstanceOf[Component]).toList
  }

  // Return a list of `InputPort`s that are connected
  def findConnections(port: OutputPort[_]): Seq[InputPort[_]] = {
    val cpFrom = cpGraph.nodes find (c => c.value.asInstanceOf[Component].equals(port.getOwner))
    val edges = cpFrom.get.edges

    // Find all connections (wires/edges) of this component
    val connections = edges filter {
      w => w.label.asInstanceOf[Wire].from == port
    }
    // Return only the InputPort of the connected component, extracted from the label of the edge
    val tos = connections.map(x => x.label.asInstanceOf[Wire].to)
    tos.toSeq

    // TODO test me
    // Comment avoir le label du egde ?
    // Chercher les edges de la source et filter celle qui va vers la destination connue
    // Ensuite prendre son label...
    // !! Peut aller vers le même composant plusieurs fois !!
    //val to = cpFrom.get.diSuccessors.head.value.asInstanceOf[Component]
    //println(to.getInputs)

    // throw new IOException("test here")
    // cp.get.diSuccessors.head.value.asInstanceOf[InputPort[_]]
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


  // TODO beautify all these methods with pattern matching

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

  //TODO Move all generate code to others class
  //TODO Differents generators with pattern matching on trait to now if it is harware or simulated ?
  def generateConstantsCode() = {

    val cst = cpGraph.nodes filter (c => c.value.isInstanceOf[hw_implemented] && c.value.isInstanceOf[Constant[_]])
    // Return a seq of Constant as Component
    val hwCst = cst.map(x => x.value.asInstanceOf[hw_implemented]).toList

    var result = "//*// generateConstantsCode\n"
    for (c <- hwCst if c.getGlobalConstants.isDefined) {
      result += c.getGlobalConstants.get + "\n"
    }
    result + "\n"
  }

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

  def generateLoopingCode() = {
    var result = "//*// generateLoopingCode\n"
    for (c ← cpGraph.nodes.toList) {
      val comp = c.value
      assert(comp.isInstanceOf[Component])

      if (comp.isInstanceOf[hw_implemented]) {
        val hw_c = comp.asInstanceOf[hw_implemented]

        if (hw_c.getLoopableCode.isDefined)
          result += "\t\t" + hw_c.getLoopableCode.get + "\n"
      }
    }
    result
  }

  // This a basically a Tuple2, but they cannot be override
  private class Wire(val from: OutputPort[_], val to: InputPort[_]) {
    override def toString = "MyWire " + from + "~" + to
  }

}