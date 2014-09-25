package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging

import scalax.collection.GraphEdge.DiEdge
import scalax.collection.mutable.Graph

// FIXME utiliser uniquement 1 generateur de ID pour tout. Avoir un convexte. Exemple, un composant doit avori un ID et ses contanstes internes aussi...
object IdGenerator {

  //TODO declare a type Id ?

  private var id = 0

  def newUniqueId = {
    id += 1
    id
  }
}

object ComponentManager extends Logging {

  // This contains a graph representation of the components
  // that can be skimmed later on
  var gr1: Graph[Component, DiEdge] = Graph.empty[Component, DiEdge]

  // TODO : this can be removed by using directly the gr1 graph
  //var comps: List[Component] = List.empty[Component]

  def registerComponent(c: Component) = {
    info(s"Register `$c` of type `${c.getClass.getSimpleName}`.")
    gr1 += c // Add the component node to the graph
    info("Size of gr1: " + gr1.size)
  }

  def addWire(from: Component, to: Component) = {
    //TODO check if node exist or error (assert)
    //assert((gr1.nodes.get(from) != null)
    //assert((gr1 get to).isIn == true)

    import scalax.collection.GraphPredef._
    gr1 += (from ~> to)
  }

  // TODO beautify all these methods with pattern matching

  // TODO beautify this (factorize it)
  def generateInitCode() = {
    var result = ""

    for (c ← gr1.nodes.toList) {
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

    for (c ← gr1.nodes.toList) {
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
    var result = ""

    for (c ← gr1.nodes.toList) {
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

    for (c ← gr1.nodes.toList) {
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
    for (c ← gr1.nodes.toList) {
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