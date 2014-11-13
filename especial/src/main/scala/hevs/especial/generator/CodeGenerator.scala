package hevs.especial.generator

import java.io.File
import java.util.Date

import hevs.especial.dsl.components.{Component, ComponentManager, hw_implemented}
import hevs.especial.simulation.QemuLogger
import hevs.especial.utils._

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
    // (hw: hw_implemented) => hw.getIncludeCode, // Must remove duplicates files
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

    // Add include files and remove duplicates files
    result ++= beginSection(0)
    result ++= includeFiles(cps)
    result ++= endSection()

    // Generic code sections for all components
    for (sec <- codeSections.zipWithIndex) {
      val idx = sec._2 + 1 // Section 0 already done

      idx match {
        case 4 =>
          result ++= beginMain
          if(ctx.isQemuLoggerEnabled)
            result ++= QemuLogger.addStartEvent + "\n"

          result ++= beginMainInit
          if(ctx.isQemuLoggerEnabled)
            result ++= QemuLogger.addEndInitEvent + "\n"
        case 5 =>
          if(ctx.isQemuLoggerEnabled)
            result ++= QemuLogger.addLoopStartEvent + "\n"
        case _ =>
      }

      result ++= beginSection(idx) // Print the section name

      // Add static code when sections start
      idx match {
        case 3 =>
          // First init all outputs
          result ++= beginOutputInit
          result ++= initOutputs()
          result ++= endInit + "\n"

          // General init
          result ++= beginInit
        case 5 => result ++= beginMainLoop
        case _ =>
      }

      // Apply the current section function on all components
      cps map { cp => sec._1(cp.asInstanceOf[hw_implemented]) match {
        // Add the code only if defined
        case Some(code) => result ++= code + "\n"
        case None =>
      }
      }

      // Add static code when sections end
      idx match {
        case 3 =>result ++= endInit
        case 5 =>
          if(ctx.isQemuLoggerEnabled)
            result ++= "\n" + QemuLogger.addLoopTickEvent
          result ++= endMainLoop
        case _ =>
      }

      result ++= endSection() // Print the end of the section
    }

    if(ctx.isQemuLoggerEnabled)
      result ++= QemuLogger.addLoopExitEvent

    // End of the file
    result ++= endMain
    result ++= endFile(ctx.progName)
    result.result()
  }

  // Include all necessary files and remove duplicates if necessary
  private def includeFiles(cps: Seq[Component]): String = {
    // List of list of all files to include
    val incs = for(c <- cps) yield c.asInstanceOf[hw_implemented].getIncludeCode

    // Remove duplicates files contains in the flatten list using `distinct`
    val files = for(f <- incs.flatten.distinct) yield String.format("#include \"%s\"", f)
    files.mkString("\n") + "\n"
  }

  // Init all outputs before the general init
  private def initOutputs(): String = {
    val ret = new StringBuilder
    ret ++= "// Initialize all connected outputs automatically\n"
    val outputs = ComponentManager.findConnectedOutputHardware
    outputs map { cp => cp.asInstanceOf[hw_implemented].getInitCode match {
      // Add the code only if defined
      case Some(code) => ret ++= code + "\n"
      case None =>
    }
    }
    ret.result()
  }

  /* Static code definitions */

  private final def beginSection(idx: Int) = "//*// Section %02d\n".format(idx)

  private final def endSection() = "//*// ----------\n\n"

  private final def beginFile(progName: String) = {
    val file = s"Code for '$progName'."
    val out = new StringBuilder
    out ++= "/*" + "\n"
    out ++= " " + "*".*(80) + "\n"
    out ++= s" $file\n"
    out ++= " " + "-".*(file.length) + "\n"
    out ++= s" Generated automatically on ${new Date()}.\n"
    out ++= s" $Version\n"
    out ++= " " + "*".*(80) + "\n"
    out ++= " */\n\n"
    out.result()
  }

  private final val beginInit = "void init() {\n"

  private final val endInit = "}\n"

  private final val beginOutputInit = "void initOutputs() {\n"

  private final val beginMain = "int main() {\n"

  private final val beginMainInit = "initOutputs();\ninit();\n\n"

  private final val beginMainLoop = "while(1) {\n"

  private final val endMainLoop = "}\n"

  private final val endMain = "}\n"

  private final def endFile(fileName: String) = s"// END of file '$fileName.cpp'"
}