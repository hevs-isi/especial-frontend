package hevs.especial.generator

import hevs.especial.dsl.components.{Component, CType, Port, ComponentManager}
import hevs.especial.utils.{Logger, Context, Pipeline, Settings}

import scala.collection.mutable

/**
 * Helper object used to check if a code has warnings or not.
 */
object CodeOptimizer {
  /**
   * A program without warning.
   * @return true if no warnings found, false otherwise
   */
  def hasNoWarning = !hasWarnings

  /**
   * A program with warnings.
   * @return true if warnings found, false otherwise
   */
  def hasWarnings: Boolean = new CodeOptimizer().checkWarnings().isDefined
}

/**
 * Check the DSL program and print some warnings.
 *
 * For now, it check only if components or inputs are not connected. This check can be disabled in the settings.
 */
class CodeOptimizer extends Pipeline[Unit, Boolean] {

  // Count the number of passes to optimize the code
  private var nbrOfPasses = 0

  /**
   * Analyse the `ComponentManager` and print some warnings if any.
   *
   * @param ctx the context of the program with the logger
   * @param input nothing (not used)
   * @return true if warnings found, false otherwise
   */
  def run(ctx: Context)(input: Unit): Boolean = {
    if (!Settings.PIPELINE_RUN_CODE_OPTIMIZER) {
      ctx.log.info(s"$currentName is disabled.")
      return false
    }

    var warns = checkWarnings()
    printWarningsOutput(ctx, warns)

    optimize(ctx)

    warns = checkWarnings()
    printWarningsOutput(ctx, warns)
    assert(warns.isEmpty) // All warnings should be removed

    warns.isDefined // Warnings detected or not
  }

  private def printWarningsOutput(ctx: Context, warns: Option[String]): Unit = {
    // Print all warnings found
    if (warns.isDefined)
      ctx.log.warn(s"Warnings found in program '${ctx.progName}':\n" + warns.get)
    else
      ctx.log.info(s"No warning found in program '${ctx.progName}'.")
  }

  /**
   * Run some checks to detect warnings in the program.
   * Check if components are completely isolated.
   * Check if ports are not connected.
   */
  private def checkWarnings(): Option[String] = {

    val out = new StringBuilder

    // Isolated components
    val c = ComponentManager.findUnconnectedComponents
    if (c.nonEmpty) {
      out ++= s"[WARN] ${c.size} "
      out ++= (if (c.size < 2) "component" else "components")
      out ++= " declared but not connected at all:\n"
      out ++= "\t- " + c.mkString("\n\t- ") + "\n\n"
    }

    // Unconnected ports
    val p = findUnconnectedPorts
    if (p.nonEmpty) {
      out ++= s"[WARN] ${p.size} unconnected "
      out ++= (if (p.size < 2) "port" else "ports")
      out ++= " found:\n"
      out ++= "\t- " + p.mkString("\n\t- ")
    }

    if (out.isEmpty)
      None // No warning
    else
      Some(out.toString())
  }

  /**
   * Return unconnected ports of all components of the graph.
   * @return all unconnected ports of all components
   */
  private def findUnconnectedPorts: Seq[Port[CType]] = {
    val ncPorts = mutable.ListBuffer.empty[Port[CType]]
    for (cp <- ComponentManager.getComponents) {
      ncPorts ++= cp.getUnconnectedPorts
    }
    ncPorts.toSeq
  }

  def optimize(ctx: Context): Unit = {

    var nbrCpToRemove = 0
    var totalCpRemoved = 0
    ctx.log.info(s"Optimizer started for '${ctx.progName}'.")
    do {
      startPass(ctx.log)

      // Count the number of components to remove
      nbrCpToRemove = ComponentManager.getComponents.foldLeft(0) {
        (acc, cp) =>
          if (canRemoveComponent(ctx.log)(cp)) {
            // Remove the component and count removing operations
            ComponentManager.removeComponent(cp.getId)
            acc + 1
          }
          else acc
      }
      ctx.log.trace(s"$nbrCpToRemove component(s) have been removed in pass ${nbrOfPasses + 1}.")

      // Run the next pass if components have been removed
      if (nbrCpToRemove > 0) {
        totalCpRemoved += nbrCpToRemove
        endPass()
      }
    }
    while (nbrCpToRemove != 0)

    ctx.log.info(s"Optimizer ended successfully after $nbrOfPasses passes. " +
      s"$totalCpRemoved component(s) have been removed.")
    ctx.log.info(s"The final graph has ${ComponentManager.numberOfNodes} nodes " +
      s"and ${ComponentManager.numberOfEdges} edges.")
  }


  // Debug only. Print the nex phase number
  private def startPass(l: Logger): Unit = {
    l.trace("Pass [%03d]".format(nbrOfPasses + 1))
  }

  // Count the number of phase
  private def endPass(): Unit = {
    nbrOfPasses += 1
  }

  /**
   * Check if the component can be removed or not.
   * It can be remove if:
   * - it has more than one input and more than one output
   * - all inputs or outputs are disconnected
   * Components without I/O are not removed.
   *
   * @param log logger to trace information
   * @param c the component to try to remove
   * @return `true` if the component can be removed, `false otherwise`
   */
  private def canRemoveComponent(log: Logger)(c: Component): Boolean = {
    var canBeRemoved = false

    // Check if all outputs are unconnected
    val outs = c.getOutputs.getOrElse(Nil)
    val nbrOutNc = outs.count(out => out.isNotConnected)
    if (nbrOutNc == outs.size && outs.size > 0) {
      log.trace(s"Remove $c: all outputs are unconnected.")
      canBeRemoved = true
    }

    // Check if all inputs are unconnected
    val ins = c.getInputs.getOrElse(Nil)
    val nbrInNc = ins.count(in => in.isNotConnected)
    if (nbrInNc == ins.size && ins.size > 0) {
      log.trace(s"Remove $c: all inputs are unconnected.")
      canBeRemoved = true
    }

    canBeRemoved // Component can be removed or not
  }
}
