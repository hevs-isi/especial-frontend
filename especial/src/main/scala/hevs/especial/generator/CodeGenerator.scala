package hevs.especial.generator

import java.io.File

import hevs.especial.dsl.components.fundamentals.hw_implemented
import hevs.especial.utils._

import scala.collection.mutable

/**
 * To generate the C code of a program, the `Resolver` class must be used to first resolve the graph,
 * and then generate the code for each connected components in the right order.
 */
class CodeGenerator extends Pipeline[Resolver.O, String] {

  /** Output path of the generated code. */
  private final val OUTPUT_PATH = "output/%s/"

  /**
   * Define all sections of the code that compose the file generated C file.
   * The order is important and correspond to the generation order.
   */
  private final val codeSections = Seq(
    (hw: hw_implemented) => hw.getIncludeCode,
    (hw: hw_implemented) => hw.getGlobalCode,
    (hw: hw_implemented) => hw.getFunctionsDefinitions,
    (hw: hw_implemented) => hw.getInitCode,
    (hw: hw_implemented) => hw.getBeginOfMainAfterInit,
    (hw: hw_implemented) => hw.getLoopableCode,
    (hw: hw_implemented) => hw.getExitCode
  )

  /**
   * Generate the C code from the DSL program using the order given by the resolver. If the resolver failed,
   * the code generator is not called.
   * The code generator write the file to the output directory. The output of the pipeline is the path of the generated
   * file.
   *
   * @param ctx the context of the program with the logger
   * @param input the result of the resolver
   * @return the path of the C generated file
   */
  def run(ctx: Context)(input: Resolver.O): String = {
    // Generate the C file (the folder should exist and created by the resolver before)
    val path = String.format(OUTPUT_PATH, ctx.progName) + ctx.progName + ".cpp"
    val f: RichFile = new File(path)

    val code = generateCode(ctx)(input)
    val res = f.write(code) // Write succeed or not
    if (res)
      ctx.log.info(s"Code generated to '$path'.")
    else
      ctx.log.info(s"Unable to generate the C code.")

    path // The file path as output
  }

  /**
   * Generate the C source code as a String.
   *
   * @param ctx the context of the program
   * @param resolve the resolver output
   * @return the C code as a String (not formatted)
   */
  private def generateCode(ctx: Context)(resolve: Resolver.O): String = {
    // Order the result of the resolver by pass number (sort by key value)
    val ordered = resolve.toSeq.sortBy(_._1)
    val cps = ordered flatMap (x => x._2) // List of components

    // Generate each code phase for each components
    ctx.log.info(s"Generate the code for ${cps.size} components with ${codeSections.size} sections.")

    // File preamble
    val result = new StringBuilder
    result ++= beginFile(ctx.progName)

    // Code sections
    for (sec <- codeSections.zipWithIndex) {
      val idx = sec._2

      if (idx == 4) result ++= beginMain()
      result ++= beginSection(idx)

      idx match {
        case 3 => result ++= beginInit()
        case 5 => result ++= beginMainLoop()
        case _ =>
      }

      // Apply the function for the current section on each components
      cps map { hw =>
        // Add the code only if defined
        sec._1(hw) match {
          case Some(code) => result ++= code + "\n"
          case None =>
        }
      }

      idx match {
        case 3 => result ++= endInit()
        case 5 => result ++= endMainLoop()
        case _ =>
      }

      result ++= endSection()
    }

    // End of the file
    result ++= endMain()
    result ++= endFile(ctx.progName)
    result.result()
  }

  /* Static code definitions */

  private final def beginSection(idx: Int) = "//*// Section %02d\n".format(idx)

  private final def endSection() = "//*// ----------\n\n"

  private final def beginFile(progName: String) = {
    val ver = Version.getVersion
    val out = new StringBuilder

    out ++= "/*" + "\n"
    out ++= " " + "*".*(60) + "\n"
    out ++= s" Version $ver\n"
    out ++= s" Code for '$progName' generated automatically.\n"
    out ++= " " + "*".*(60) + "\n"
    out ++= " */\n\n"
    out.result()
  }

  private final def beginInit() = "void init() {\n"

  private final def endInit() = "}\n"

  private final def beginMain() = "int main() {\ninit();\n"

  private final def beginMainLoop() = "while(1) {\n"

  private final def endMainLoop() = "}\n"

  private final def endMain() = "}\n"

  private final def endFile(fileName: String) = s"// END of file '$fileName.c'"
}