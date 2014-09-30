package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.utils.ComponentNotFound

import scalax.collection.GraphEdge.DiEdge
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
  val cpGraph: Graph[Component, DiEdge] = Graph.empty[Component, DiEdge]

  /**
   * Insert a component in the graph. Each component has a unique ID. It cannot appears more than once in the graph.
   * If it is already in the graph,  it will not be added.
   * @param c the component to add as node in the graph
   */
  def registerComponent(c: Component) = {
    info(s"Register component $c with id ${c.id}.")
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

    // Add the connection (wire) between these to ports
    import scalax.collection.GraphPredef._
    cpGraph += (cpFrom ~> cpTo)
  }

  /**
   * Search for a component the graph nodes by id. If the component is not found, an exception is thrown.
   * @param cpId the component id to search
   * @return the component node or an exception if not found
   */
  private def cp(cpId: Int): Component = {
    // Find a component by ID. Exist once only.
    val cp = cpGraph.nodes find (c => c.value.asInstanceOf[Component].id == cpId)
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


  // TODO beautify all these methods with pattern matching

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

  //TODO Move all generate code to others class
  //TODO Differents generators with pattern matching on trait to now if it is harware or simulated ?
  def generateConstantsCode() = {

    val cp = cpGraph.nodes filter (c => c.value.isInstanceOf[hw_implemented])
    // Return a seq of components
    val hwCp = cp.map(x => x.value.asInstanceOf[Component]).toList

    println("Cst CP: " + cp.mkString("--"))
    println("Cst hw CP: " + hwCp.mkString("--"))

    //TODO: check this




    var result = ""

    for (c ← cpGraph.nodes.toList) {
      val comp = c.value
      assert(comp.isInstanceOf[Component])

      if (comp.isInstanceOf[hw_implemented]) {
        val hw_c = comp.asInstanceOf[hw_implemented]

        if (hw_c.getGlobalConstants.isDefined)
          result += hw_c.getGlobalConstants.get + "\n"
      }
    }
    result
  }

  def generateFunctionsCode() = {
    var result = ""

    // Works but not really clearer
    //		val filteredInstances : List[hw_implemented] = comps.filter(_.isInstanceOf[hw_implemented]).map(_.asInstanceOf[hw_implemented])
    //		val functionCode = filteredInstances.map(x => x.getFunctionsDefinitions()).foldLeft("")(_ + _)

    for (c ← cpGraph.nodes.toList) {
      val comp = c.value
      assert(comp.isInstanceOf[Component])

      if (comp.isInstanceOf[hw_implemented]) {
        val hw_c = comp.asInstanceOf[hw_implemented]

        if (hw_c.getFunctionsDefinitions.isDefined)
          result += hw_c.getFunctionsDefinitions.get + "\n"
      }
    }

    result
  }

  def generateLoopingCode() = {
    var result = ""
    // TODO find a guard for making writable on a single line (for refactoring)
    //for (c ← gr1.nodes.toList; if c.value.isInstanceOf[hw_implemented]) { // TODO Does not work, why ?
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
}