package hevs.especial.generator

import java.io.File
import java.util.Date

import hevs.especial.dsl.components.{Component, ComponentManager, HwImplemented}
import hevs.especial.simulation.QemuLogger
import hevs.especial.utils._

/**
 * Output C/C++ code generator corresponding to the DSL program.
 *
 * To generate the C/C++ code of a program, the [[Resolver]] is used to resolve the graph, and then the code for each
 * connected components can be generated in the right order. Each component generates is responsible to generated its
 * own code. The generated code file is divided in different sections and every component can add a part of code in
 * each of them, depending on its needs.
 *
 * The `while` loop is divided in 3 sections: 1) Read inputs 2) Loop logic and 3) Update outputs.
 * Comments will be added to the generated code if [[Settings.GEN_VERBOSE_CODE]] is set.
 *
 * @author Christopher Metrailler (mei@hevs.ch)
 * @version 2.0
 */
class CodeGenerator extends Pipeline[Resolver.O, String] {

  /** Output path of the generated code. */
  private final val OUTPUT_PATH = "output/%s/"

  /**
   * Define all sections of the code that compose the file generated C/C++ file.
   * The order is important and correspond to the generation order.
   */
  private final val codeSections = Seq(
    // (hw: hw_implemented) => hw.getIncludeCode, // Must remove duplicates files
    (hw: HwImplemented) => hw.getGlobalCode,
    (hw: HwImplemented) => hw.getFunctionsDefinitions,
    (hw: HwImplemented) => hw.getInitCode,
    (hw: HwImplemented) => hw.getBeginOfMainAfterInit,
    (hw: HwImplemented) => hw.getLoopableCode,
    (hw: HwImplemented) => hw.getExitCode
  )

  /**
   * Generate the C/C++ code from the DSL program using the order given by the resolver.
   *
   * If the resolver failed, the code generator is not called. The code generator write the file to the output
   * directory. The output of the pipeline is the path of the generated file.
   *
   * @param ctx the context of the program with the logger
   * @param input the result of the resolver
   * @return the path of the generated file
   */
  def run(ctx: Context)(input: Resolver.O): String = {
    // Test if the output directory already exist
    val dirPath = String.format(OUTPUT_PATH, ctx.progName)
    val dir: RichFile = new File(dirPath)
    dir.createFolder() // Create if not exist only

    // Generate the C file to the output folder
    val path = dirPath + ctx.progName + ".cpp"
    val f: RichFile = new File(path)

    val code = generateCode(ctx)(input)
    val res = f.write(code) // Write succeed or not
    if (res)
      ctx.log.info(s"Code generated to '$path'.")
    else
      ctx.log.error(s"Unable to save the generated code to '$path'.")

    path // The file path as output
  }

  /**
   * Generate the C/C++ source code as a String.
   *
   * @param ctx the context of the program
   * @param resolve the resolver output
   * @return the C/C++ code generated as a String (not formatted)
   */
  private def generateCode(ctx: Context)(resolve: Resolver.O): String = {
    // Order the result of the resolver by pass number (sort by key value).
    // Each pass number as a sequence of components to generate.
    val ordered = resolve.toSeq.sortBy(_._1)

    // List with components only, ordered for the code generation
    val cps = ordered flatMap (x => x._2)
    val firstLogicIdx = ordered.head._2.size // Count the number of input component (first pass)

    val nbrOfOutputs = ordered.last._2.size // Number of output (last pass)
    val firstOutputIndex = cps.size - nbrOfOutputs

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
          if (ctx.isQemuLoggerEnabled)
            result ++= QemuLogger.addStartEvent + "\n"

          result ++= beginMainInit
          if (ctx.isQemuLoggerEnabled)
            result ++= QemuLogger.addEndInitEvent + "\n"
        case 5 =>
          if (ctx.isQemuLoggerEnabled)
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
      cps.zipWithIndex map { c =>

        val cpNbr = c._2 // Iteration number
        val cp = c._1 // Component to generate

        // While loop code section for the first component
        if (idx == 5 && cpNbr == 0) {

          // QEMU logger. Ack event to start one loop iteration.
          if (ctx.isQemuLoggerEnabled)
            result ++= "\n" + QemuLogger.addLoopTickEvent + "\n"

          if (Settings.GEN_VERBOSE_CODE)
            result ++= "// 1) Read inputs"
          result ++= "\n"
        }

        if (idx == 5 && cpNbr == firstLogicIdx) {
          if (Settings.GEN_VERBOSE_CODE)
            result ++= "\n// 2) Loop logic"
          result ++= "\n"
        }

        if (idx == 5 && cpNbr == firstOutputIndex) {
          if (Settings.GEN_VERBOSE_CODE)
            result ++= "\n// 3) Update outputs"
          result ++= "\n"
        }

        // Add the component code for the section (if defined)
        sec._1(cp.asInstanceOf[HwImplemented]) match {
          case Some(code) =>
            result ++= code + "\n"
          case None =>
        }
      }

      // Add static code when sections end
      idx match {
        case 3 => result ++= endInit
        case 5 => result ++= endMainLoop
        case _ =>
      }

      result ++= endSection() // Print the end of the section
    }

    if (ctx.isQemuLoggerEnabled)
      result ++= QemuLogger.addLoopExitEvent

    // End of the file
    result ++= endMain
    result ++= endFile(ctx.progName)
    result.result()
  }

  // Include all necessary header files, needed by the components. Remove duplicate files.
  private def includeFiles(cps: Seq[Component]): String = {
    // List of list of all files to include
    val incs = for (c <- cps) yield c.asInstanceOf[HwImplemented].getIncludeCode

    // Remove duplicates files contains in the flatten list using `distinct`
    val files = for (f <- incs.flatten.distinct) yield String.format("#include \"%s\"", f)
    files.mkString("\n") + "\n"
  }

  // Init all outputs before the general init
  private def initOutputs(): String = {
    val ret = new StringBuilder
    val outputs = ComponentManager.findConnectedOutputHardware
    outputs map { cp => cp.asInstanceOf[HwImplemented].getInitCode match {
      // Add the code only if defined
      case Some(code) => ret ++= code + "\n"
      case None =>
    }
    }
    ret.result()
  }

  /* Static code definitions */

  private final def beginSection(idx: Int) = Settings.GEN_VERBOSE_CODE match {
    // Print the name of the section only if the output is verbose
    case true => "//*// Section %02d\n".format(idx)
    case _ => ""
  }

  private final def endSection() = Settings.GEN_VERBOSE_CODE match {
    case true => "//*// ----------\n\n"
    case _ => "\n"
  }

  // Header of the file. Comments describing the program and the version used.
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