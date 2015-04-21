package hevs.especial.generator

import hevs.especial.utils.OSUtils.{Linux, Windows}
import hevs.especial.utils._

/**
 * Format the generated code using AStyle. Can be disabled using the `Settings` class.
 */
class CodeFormatter extends Pipeline[String, String] {

  /**
   * Format the generated C code using AStyle and write the file to the output directory.
   *
   * @param ctx the context of the program with the logger
   * @param input the path of the generated file
   * @return the path of the formatted file or the input file path if an error occurs
   */
  def run(ctx: Context)(input: String): String = {
    if (!Settings.PIPELINE_RUN_FORMATTER) {
      ctx.log.info(s"$currentName is disabled.")
      return input // Return the path of the none formatted file
    }

    // Call the AStyle conversion program
    val path = OSUtils.getOsType match {
      case Windows => Settings.ASTYLE_PATH.format("astyle.exe")
      case Linux => Settings.ASTYLE_PATH.format("astyle")
      case _ =>
        ctx.log.error("OS not supported. Cannot run AStyle !")
        return input // Return the path of the none formatted file
    }

    val valid = OSUtils.runWithCodeResult(path + " -V")
    if (valid._1 == 0) {
      ctx.log.info(s"Running '${valid._2}'.")
      OSUtils.runWithResult(path + s" --style=java -Y $input")
    }
    else {
      ctx.log.error(s"Unable to run AStyle !\n > ${valid._2}")
      return input // Return the path of the none formatted file
    }

    // The original C code is automatically renamed by AStyle to ".orig"
    ctx.log.info("Formatted code generated.")
    input
  }
}
