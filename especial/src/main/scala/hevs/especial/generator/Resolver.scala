package hevs.especial.generator

import hevs.especial.dsl.components.{Component, ComponentManager}
import hevs.especial.generator.Resolver.O
import hevs.especial.utils.{Context, Logger, Pipeline, Settings}

import scala.collection.mutable

object Resolver {
  // Output type of the resolver and the input of code generator.
  type O = Map[Int, Set[Component]] // Shared by different blocks
}

/**
 * The `Resolver` object is be used to resolve the component graph.
 *
 * The code of each component must be generated in the good order. First, the code of all input is generated. Then
 * its components successors, and finally all outputs together at the end.
 * Unconnected components are ignored.
 *
 * @version 2.0
 */
class Resolver extends Pipeline[Unit, O] {

  // IDs of components that are generated. Order not valid !
  private val generatedCpId = mutable.Set.empty[Int]

  // Component code already generated or not
  private val nextPassCpId = mutable.Set.empty[Int]

  // Count the number of passes to generate the code
  private var nbrOfPasses = -1

  // The result of the solver. The index is the pass number.
  private val mapSolve = mutable.Map.empty[Int, Set[Component]]

  /**
   * Resolve the current graph.
   *
   * @param ctx the context of the program with the logger
   * @param input nothing (not used)
   * @return the resolver graph
   */
  def run(ctx: Context)(input: Unit): O = resolve(ctx.log)

  /**
   * Resolve a graph of components. Unconnected components are ignored.
   * The resolver do nothing if there is less than two connected components. After a maximum of `MaxPasses` iterations,
   * the `Resolver` stops automatically. An empty `Map` is returned if the resolver failed. The index of the returned
   * Map is the pass number.
   *
   * At pass '0', all inputs codes are generated.
   * Then, all components and successors in the right order.
   * The final pass are all output components.
   *
   * @param log the reporter logger
   * @return ordered Map of hardware to resolve, with the pass number as index
   */
  private def resolve(log: Logger): O = {

    nbrOfPasses = 0 // Reset
    val connectedNbr = ComponentManager.numberOfConnectedHardware()
    val unconnectedNbr = ComponentManager.numberOfUnconnectedHardware()

    // At least two components must be connected together, or nothing to resolve...
    if (connectedNbr == 1) {
      // Nothing to resolve. Just return the component id
      val warn = s"Nothing to resolve: $connectedNbr connected " +
        (if (connectedNbr < 2) "component" else "components") + s" ($unconnectedNbr unconnected)"
      log.warn(warn)
      mapSolve += (0 -> Set(ComponentManager.findConnectedInputHardware.head))
      return mapSolve.toMap
    }
    else if (connectedNbr == 0) {
      // Warning. No connected component found ?
      log.warn(s"No connected component found ($unconnectedNbr unconnected)")
      return Map.empty
    }

    // Normal case
    log.trace(s"Resolver started for $connectedNbr components ($unconnectedNbr unconnected)")

    do {
      mapSolve += (nbrOfPasses -> nextPass(log)) // Resolve each pass for the current component graph
    } while (generatedCpId.size != connectedNbr && nbrOfPasses < Settings.RESOLVER_MAX_PASSES)

    // Last pass. Generate code for all outputs.
    val out = genConnectedOutput(log)
    mapSolve += (nbrOfPasses -> genConnectedOutput(log))

    if (generatedCpId.size == connectedNbr) {
      log.trace(s"Resolver ended successfully after $numberOfPasses passes for ${generatedCpId.size} connected " +
        s"component(s).")
      log.info(printResult())
      return mapSolve.toMap // Return all components in the right order (immutable Map)
    }

    // Error: infinite loop. Report an error.
    log.error(s"Error resolving the graph. Stopped after $numberOfPasses passes.")
    Map.empty
  }

  /**
   * Return the number of passes necessary to resolve the graph.
   * If it was not possible to resolve it, `RESOLVER_MAX_PASSES` is returned.
   *
   * @return the number of passes executed to resolve the graph
   */
  def numberOfPasses = nbrOfPasses

  private def genConnectedOutput(l: Logger): Set[Component] = {
    val out = ComponentManager.findConnectedOutputHardware
    for (c <- out if !generatedCpId.contains(c.getId)) {
      l.trace(s" > Generate code for output: $c")
      val cp = c.asInstanceOf[Component]
      codeGeneratedFor(cp.getId)
    }
    endPass()
    out // HW component to generate
  }

  /**
   * Compute one pass of resolving the graph.
   * @return component to generate for this pass
   */
  private def nextPass(l: Logger): Set[Component] = {

    startPass(l)

    nbrOfPasses match {

      // First passe. Generate code for all inputs.
      case 0 =>
        val in = ComponentManager.findConnectedInputHardware
        for (c <- in) {
          l.trace(s" > Generate code for: $c")
          val cp = c.asInstanceOf[Component]
          codeGeneratedFor(cp.getId)
        }

        // All output are added manually at the end
        val out = ComponentManager.findConnectedOutputHardware
        for (c <- out) {
          val cp = c.asInstanceOf[Component]
          generatedCpId += cp.getId
        }

        endPass()
        in // HW component to generate


      // From the second passe, the code of all direct successors of the components is generated.
      case _ =>
        val genCp = mutable.Set.empty[Component]
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
                l.trace(s" > Not ready: $cp")
              }
              else {
                l.trace(s" > Generate code for: $cp")
                codeGeneratedFor(cp.getId)
                genCp += cp
              }
            }
            else {
              // Code of this component already generated
              l.trace(s" > Already done for: $cp")
            }
          }
        }
        endPass()
        genCp.toSet // HW component to generate (immutable Set)
    }
  }

  private def startPass(l: Logger) = {
    // Debug only. Print the next pass number
    l.trace("Pass [%03d]".format(nbrOfPasses + 1))
  }

  private def endPass() = {
    nbrOfPasses += 1 // Count the number of passes
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

  // Print the resolver result in a pretty format.
  private def printResult(): String = {
    val ret = new mutable.StringBuilder()
    ret ++= "Resolver result:"

    // Print components IDs for all passes and order the result
    for (p <- mapSolve.toSeq.sortBy(_._1)) {
      ret ++= "\n\tPass %03d: %s".format(p._1 + 1, p._2.mkString(", "))
    }
    ret.result()
  }
}
