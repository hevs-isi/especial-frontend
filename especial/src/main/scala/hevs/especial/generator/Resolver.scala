package hevs.especial.generator

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.fundamentals.{Component, hw_implemented}
import hevs.especial.generator.Resolver.O
import hevs.especial.utils.{Context, Logger, Pipeline}

import scala.collection.mutable

object Resolver {
  // Output type of the resolver. Used by the Code generator.
  type O = Map[Int, Set[hw_implemented]]
}

/**
 * The `Resolver` object can be used to resolve a graph of components to get the right order on which component's
 * code must be generated when.
 * Unconnected components are ignored.
 */
class Resolver extends Pipeline[Any, O] with Logging {

  // Define the maximum number of passes. After that, the resolver will stop.
  private val MaxPasses = 64 // Should be enough for now

  // IDs of components that are generated. Order not valid !
  private val generatedCpId = mutable.Set.empty[Int]

  // Component code already generated or not
  private val nextPassCpId = mutable.Set.empty[Int]

  // Count the number of passes to generate the code
  private var nbrOfPasses = 0

  /**
   * Resolve the current graph.
   *
   * @param ctx the context of the program with the logger
   * @param input nothing (not used)
   * @return the resolver graph
   */
  def run(ctx: Context)(input: Any): O = resolve(ctx.log)

  /**
   * Resolve a graph of components. Unconnected components are ignored.
   * The resolver do nothing if there is less than two connected components. After a maximum of `MaxPasses` iterations,
   * the `Resolver` stops automatically. An empty `Map` is returned if the resolver failed. The index of the returned
   * Map is the pass number.
   *
   * @param log the reporter logger
   * @return ordered Map of hardware to resolve, with the pass number as index
   */
  private def resolve(log: Logger): O = {

    val connectedNbr = ComponentManager.numberOfConnectedHardware()
    val unconnectedNbr = ComponentManager.numberOfUnconnectedHardware()
    val map = mutable.Map.empty[Int, Set[hw_implemented]]

    // At least two components must be connected together, or nothing to resolve...
    if (connectedNbr == 1) {
      // Nothing to resolve. Just return the component id
      log.warn(s"Nothing to resolve: $connectedNbr components ($unconnectedNbr unconnected)")
      map += (0 -> Set(ComponentManager.findConnectedInputHardware.head))
      return map.toMap
    }
    else if (connectedNbr == 0) {
      // Warning. No connected component found ?
      log.warn(s"No connected components found ($unconnectedNbr unconnected)")
      return Map.empty
    }

    // Normal case
    trace(s"Resolver started for $connectedNbr components ($unconnectedNbr unconnected)")
    do {
      map += (nbrOfPasses -> nextPass) // Resolve each pass for the current component graph
    } while (generatedCpId.size != connectedNbr && nbrOfPasses < MaxPasses)

    if (generatedCpId.size == connectedNbr) {
      trace(s"Resolver ended successfully after $getNumberOfPasses passes for ${generatedCpId.size} connected " +
        s"components")
      return map.toMap // Return all components in the right order (immutable Map)
    }

    // Error: infinite loop. Report an error.
    log.error(s"Error resolving the graph. Stopped after $getNumberOfPasses passes.")
    Map.empty
  }

  /**
   * Return the number of passes necessary to resolve the graph. If it was not possible to resolve it,
   * `MaxPasses` is returned.
   * @return number of passes to resolve thr graph
   */
  def getNumberOfPasses = nbrOfPasses

  /**
   * Compute one pass of resolving the graph.
   * @return component to generate for this pass
   */
  private def nextPass: Set[hw_implemented] = {

    startPass()

    nbrOfPasses match {
      // First passe. Generate code for all inputs.
      case 0 =>
        val in = ComponentManager.findConnectedInputHardware
        for (c <- in) {
          trace(s" > Generate code for: $c")
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
                trace(s" > Not ready: $cp")
              }
              else {
                trace(s" > Generate code for: $cp")
                codeGeneratedFor(cp.getId)
                genCp += cp.asInstanceOf[hw_implemented]
              }
            }
            else {
              // Code of this component already generated
              trace(s" > Already done for: $cp")
            }
          }
        }
        endPass()
        genCp.toSet // HW component to generate (immutable Set)
    }
  }

  // Debug only. Print the nex phase number
  private def startPass() = {
    trace("Pass [%03d]".format(nbrOfPasses + 1))
  }

  // Count the number of phase
  private def endPass() = {
    nbrOfPasses += 1
  }

  // Components code generated. Save it to do it once only.
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
