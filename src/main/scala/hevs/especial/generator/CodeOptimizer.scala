package hevs.especial.generator

import hevs.especial.dsl.components.{Component, ComponentManager}
import hevs.especial.utils.{Context, Logger, Pipeline, Settings}

/**
 * Optimize the program graph.
 *
 * Remove unused and unconnected components. If all inputs or outputs of a component are disconnected,
 * it is considered as useless and can be removed from the graph, with all its edges. Before removing the component,
 * all connected ports are disconnected.
 *
 * Several passes are necessary to remove all unconnected components. The optimizer directly update the component graph.
 *
 * @author Christopher Metrailler (mei@hevs.ch)
 * @version 1.0
 */
class CodeOptimizer extends Pipeline[Unit, Boolean] {

  // Count the number of passes to optimize the code
  private var nbrOfPasses = -1

  // Total of removed components from the original graph
  private var totalCpRemoved = -1

  /**
   * Optimize the component graph of the current program.
   *
   * @param ctx the context of the program with the logger
   * @param input nothing (not used)
   * @return `true` if the optimizer is enabled, `false` otherwise
   */
  def run(ctx: Context)(input: Unit): Boolean = {
    if (!Settings.PIPELINE_RUN_CODE_OPTIMIZER) {
      ctx.log.info(s"$currentName is disabled.")
      return false
    }
    optimize(ctx) // Run the optimizer
    true
  }

  /**
   * The optimizer function.
   * @param ctx the context used to report information
   */
  private def optimize(ctx: Context): Unit = {
    var nbrCpToRemove = 0 // Components removed in the current pass

    // Reset
    nbrOfPasses = 0
    totalCpRemoved = 0

    ctx.log.info(s"Optimizer started for '${ctx.progName}'.")
    do {
      // Debug only. Print the next pass number
      ctx.log.trace("Pass [%03d]".format(nbrOfPasses + 1))

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
      ctx.log.trace(s" > $nbrCpToRemove component(s) removed in pass ${nbrOfPasses + 1}.")

      // Run the next pass if components have been removed
      if (nbrCpToRemove > 0)
        totalCpRemoved += nbrCpToRemove

      nbrOfPasses += 1 // Count the number of passes
    }
    while (nbrCpToRemove != 0)

    // Success. Print useful information
    ctx.log.info(s"Optimizer ended successfully after $numberOfPasses passes. " +
      s"$numberOfRemovedCmp component(s) removed.")
    ctx.log.info(s"The final graph has ${ComponentManager.numberOfNodes} node(s) " +
      s"and ${ComponentManager.numberOfEdges} edge(s).")
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
      log.trace(s" > Remove $c: all outputs are unconnected.")
      canBeRemoved = true
    }

    // Check if all inputs are unconnected
    val ins = c.getInputs.getOrElse(Nil)
    val nbrInNc = ins.count(in => in.isNotConnected)
    if (nbrInNc == ins.size && ins.size > 0) {
      log.trace(s" > Remove $c: all inputs are unconnected.")
      canBeRemoved = true
    }
    canBeRemoved
  }

  /**
   * Return the number of passes necessary to optimize the graph.
   *
   * @return the number of passes executed to optimize the graph
   */
  def numberOfPasses = nbrOfPasses

  /**
   * Return the number of removed components.
   *
   * @return the number of removed component from the original graph
   */
  def numberOfRemovedCmp = totalCpRemoved
}
