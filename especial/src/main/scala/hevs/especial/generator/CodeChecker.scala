package hevs.especial.generator

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.utils.{Context, Pipeline, Settings}

/**
 * Helper object to check if a code has warnings or not.
 */
object CodeChecker {
  /**
   * A program without warning.
   * @return true if no warnings found, false otherwise
   */
  def hasNoWarning = !hasWarnings

  /**
   * A program with warnings.
   * @return true if warnings found, false otherwise
   */
  def hasWarnings: Boolean = new CodeChecker().checkWarnings().isDefined
}

/**
 * Check the DSL program and print some warnings.
 *
 * For now, it check only if components or inputs are not connected. This check can be disabled in the settings.
 */
class CodeChecker extends Pipeline[Unit, Boolean] {

  /**
   * Analyse the `ComponentManager` and print some warnings if any.
   *
   * @param ctx the context of the program with the logger
   * @param input nothing (not used)
   * @return true if warnings found, false otherwise
   */
  def run(ctx: Context)(input: Unit): Boolean = {
    if (!Settings.PIPELINE_RUN_CODE_CHECKER) {
      ctx.log.info(s"$currentName is disabled.")
      return false
    }

    // Print all warnings found
    val warns = checkWarnings()
    if (warns.isDefined)
      ctx.log.warn("Warnings found:\n" + warns.get)
    else
      ctx.log.info("No warning found.")

    warns.isDefined // Warning found or not
  }

  /**
   * Run some checks to detect warnings in the DSL program.
   */
  private def checkWarnings(): Option[String] = {

    val out = new StringBuilder

    // Unconnected components
    val c = ComponentManager.findUnconnectedComponents
    if (c.nonEmpty) {
      out ++= s"[WARN] ${c.size} "
      out ++= (if(c.size < 2) "component" else "components")
      out ++= " declared but not connected at all:\n"
      out ++= "\t- " + c.mkString("\n\t- ") + "\n\n"
    }

    // Unconnected ports
    val p = ComponentManager.findUnconnectedPorts
    if (p.nonEmpty) {
      out ++= s"[WARN] ${p.size} unconnected "
      out ++= (if(p.size < 2) "port" else "ports")
      out ++= " found:\n"
      out ++= "\t- " + p.mkString("\n\t- ")
    }

    if (out.isEmpty)
      None // No warning
    else
      Some(out.toString())
  }
}
