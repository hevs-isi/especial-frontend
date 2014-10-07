package hevs.androiduino.dsl.generator

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.fundamentals.{Component, hw_implemented}

import scala.collection.mutable

// FIXME: better: return a map with index = number of pass and components

/**
 * The `Resolver` object can be used to resolve a graph of components to get the right order on which component's
 * code must be generated when.
 * Unconnected components are ignored.
 */
object Resolver extends Logging {

  // IDs of components that are generated. Order not valid !
  private val generatedCpId = mutable.Set.empty[Int]

  // Component code already generated or not
  private val nextPassCpId = mutable.Set.empty[Int]

  // Define the maximum number of passes. After that, the resolver will stop.
  private val MaxPasses = 20

  // Count the number of passes to generate the code
  private var nbrOfPasses = 0

  def resolve(): Map[Int, Set[hw_implemented]] = {
    reset() // First reset the state of the resolver
    resolveGraph()
  }

  /**
   * Reset the state of the current `Resolver` object.
   */
  private def reset(): Unit = {
    generatedCpId.clear()
    nextPassCpId.clear()
    nbrOfPasses = 0
  }

  /**
   * Resolve a graph of components. Unconnected components are ignored.
   * The resolver do nothing if there is less than two connected components. After a maximum of `MaxPasses` iterations,
   * the `Resolver` stops automatically. An empty `Map` is returned if there is nothing to resolve or if it fails.
   * @return the Map of hardware to resolve - in the right order, with the pass number as index
   */
  private def resolveGraph(): Map[Int, Set[hw_implemented]] = {

    val connectedNbr = ComponentManager.numberOfConnectedHardware()
    val unconnectedNbr = ComponentManager.numberOfUnconnectedHardware()

    // At least two components must be connected together, or nothing to resolve...
    if (connectedNbr < 2) {
      info(s"Nothing to resolve: $connectedNbr components ($unconnectedNbr unconnected)")
      return Map.empty // Nothing to resolve
    }

    info(s"Resolver started for $connectedNbr components ($unconnectedNbr unconnected)")

    val map = mutable.Map.empty[Int, Set[hw_implemented]]
    do {
      map += (nbrOfPasses -> nextPass) // Resolve each pass for the current component graph
    } while (generatedCpId.size != connectedNbr && nbrOfPasses < MaxPasses)

    if (generatedCpId.size == connectedNbr) {
      info(s"Resolver ended successfully after $getNumberOfPasses passes for ${generatedCpId.size} connected " +
        s"components")
      return map.toMap // Return all components in the right order (immutable Map)
    }

    error(s"Resolver stopped after $getNumberOfPasses passes")
    Map.empty // Infinite loop
  }

  def getNumberOfPasses = nbrOfPasses

  private def nextPass: Set[hw_implemented] = {

    startPass()

    nbrOfPasses match {
      // First passe. Generate code for all inputs.
      case 0 =>
        val in = ComponentManager.findConnectedInputHardware
        for (c <- in) {
          info(s" > Generate code for: $c")
          val cp = c.asInstanceOf[Component]
          codeGeneratedFor(cp.getId)
        }
        endPass()
        in // HW component to generate

      // From the second passe, the code of all direct successors of the components is generated.
      case _ =>
        val genCp = mutable.Set.empty[hw_implemented]
        val genId = nextPassCpId.clone()
        nextPassCpId.clear()

        for (id <- genId) {
          // Find direct successors for all components of the previous phase
          val successors = ComponentManager.getNode(id).diSuccessors
          for (s <- successors) {
            val cp = s.value.asInstanceOf[Component]
            val cpGen = isCodeGenerated(cp.getId)
            if (!cpGen) {
              // Check if the component inputs are ready or not
              val predecessors = ComponentManager.getNode(cp.getId).diPredecessors
              val pIds = predecessors.map(x => x.value.asInstanceOf[Component].getId)
              val ready = isCodeGenerated(pIds)
              if (!ready) {
                info(s" > Not ready: $cp")
              }
              else {
                info(s" > Generate code for: $cp")
                codeGeneratedFor(cp.getId)
                genCp += cp.asInstanceOf[hw_implemented]
              }
            }
            else {
              // Code of this component already generated
              info(s" > Already done for: $cp")
            }
          }
        }
        endPass()
        genCp.toSet // HW component to generate (immutable Set)
    }
  }

  private def startPass() = {
    info("Pass [%03d]".format(nbrOfPasses + 1))
  }

  private def endPass() = {
    nbrOfPasses += 1 // Count the number of phase
  }

  private def codeGeneratedFor(cpId: Int) = {
    generatedCpId += cpId // Add ID to the global list
    nextPassCpId += cpId // Code to generate on the next pass
  }

  // Check if the code of the component has been already generated or not
  private def isCodeGenerated(cpId: Int): Boolean = generatedCpId contains cpId

  // Check if the list of IDs has been already generated or not
  private def isCodeGenerated(cpListId: Set[Int]) = cpListId.foldLeft(true) {
    (acc, id) => acc & generatedCpId.contains(id)
  }
}
