package hevs.especial.generator

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.utils.{Logger, Pipeline, Settings}

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
class CodeChecker extends Pipeline[Any, Boolean] {

  /**
   * Analyse the `ComponentManager` and print some warnings if any.
   * @param input nothing (not used)
   * @return true if warnings found, false otherwise
   */
  def run(log: Logger)(input: Any): Boolean = {
    if (!Settings.PIPELINE_RUN_CODE_CHECKER) {
      log.info(s"$name is disabled.")
      return false
    }

    val warns = checkWarnings()
    if (warns.isDefined)
      // Print warnings
      log.warn("WARNINGS:\n\n" + warns.get)
    else
      log.info("No warning found.")

    warns.isDefined // Warning found or not
  }

  /**
   * Run some checks to detect warnings.
   */
  private def checkWarnings(): Option[String] = {

    val out = new StringBuilder

    // Unconnected components
    val c = ComponentManager.findUnconnectedComponents
    if (c.nonEmpty) {
      out ++= s"WARN: ${
        c.size
      } component(s) declared but not connected at all:\n"
      out ++= "\t- " + c.mkString("\n\t- ") + "\n\n"
    }

    // Unconnected ports
    val p = ComponentManager.findUnconnectedPorts
    if (p.nonEmpty) {
      out ++= s"WARN: ${
        p.size
      } unconnected port(s) found:\n"
      out ++= "\t- " + p.mkString("\n\t- ") + "\n"
    }

    if (out.isEmpty)
      None // No warning
    else
      Some(out.toString())
  }
}
