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

  /** List of components to generate (from the resolver). */
  private val cps = mutable.ListBuffer.empty[hw_implemented]

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
    val path = String.format(OUTPUT_PATH, ctx.progName) + ctx.progName + ".c"
    val f: RichFile = new File(path)

    val code = generateCode(ctx.progName, input)
    val res = f.write(code) // Write succeed or not
    if (res)
      ctx.log.info(s"Code generated to '$path'.")
    else
      ctx.log.info(s"Unable to generate the C code.")

    path // The file path as output
  }

  /**
   * Generate the C code.
   * @param progName the name of the C program
   * @param resolve the resolver output
   * @return the C code as a String (not formatted)
   */
  private def generateCode(progName: String, resolve: Resolver.O): String = {
    // Order the result of the resolver by pass number (sort by key value)
    val ordered = resolve.toSeq.sortBy(_._1)
    cps ++= ordered flatMap (x => x._2)

    // Generate the code
    val result = new StringBuilder
    result ++= preamble(progName)
    result ++= generateGlobalCode()
    result ++= generateFunctionsCode()
    result ++= preInit()
    result ++= generateInitCode()
    result ++= postInit()
    result ++= beginMain()
    result ++= generateBeginMainCode()
    result ++= beginLoopMain()
    result ++= generateLoopingCode()
    result ++= endLoopMain()
    result ++= endMain(progName)
    result.result()
  }

  private def preamble(progName: String) = {
    val ver = Version.getVersion
    val out = new StringBuilder

    out ++= "/*" + "\n"
    out ++= " " + "*".*(60) + "\n"
    out ++= s" Version $ver\n"
    out ++= s" Code of '$progName.c' generated automatically.\n"
    out ++= " " + "*".*(60) + "\n"
    out ++= " */\n\n"
    out ++= "#include \"target.h\"\n\n"
    out.result()
  }

  // FIXME: refactor to on generic function
  private def generateGlobalCode() = {
    val out = new StringBuilder
    out ++= "//*// 1. generateGlobalCode\n"
    for (c <- cps if c.getGlobalCode.isDefined) {
      out ++= c.getGlobalCode.get + "\n"
    }
    out + "//*// --\n\n"
  }

  // FIXME: refactor to on generic function
  private def generateFunctionsCode() = {
    val out = new StringBuilder
    out ++= "//*// 2. generateFunctionsCode\n"
    for (c <- cps if c.getFunctionsDefinitions.isDefined) {
      out ++= c.getFunctionsDefinitions.get + "\n"
    }
    out + "//*// --\n\n"
  }

  // FIXME: refactor to on generic function
  private def generateInitCode() = {
    val out = new StringBuilder
    out ++= "//*// 3. generateInitCode\n"
    for (c <- cps if c.getInitCode.isDefined) {
      out ++= c.getInitCode.get + "\n"
    }
    out + "//*// --\n"
  }

  // FIXME: refactor to on generic function
  private def generateBeginMainCode() = {
    val out = new StringBuilder
    out ++= "//*// 4. generateBeginMainCode\n"
    for (c <- cps if c.getBeginOfMainAfterInit.isDefined) {
      out ++= c.getBeginOfMainAfterInit.get + "\n"
    }
    out + "//*// --\n\n"
  }

  // FIXME: refactor to on generic function
  private def generateLoopingCode() = {
    val out = new StringBuilder
    out ++= "//*// 5. generateLoopingCode\n"
    for (c <- cps if c.getLoopableCode.isDefined) {
      out ++= c.getLoopableCode.get + "\n"
    }
    out + "//*// --\n"
  }

  private def preInit() = "void init() {\n"

  private def postInit() = "}\n\n"

  private def beginMain() = "int main() {\n"

  private def beginLoopMain() = "while(1){\n"

  private def endLoopMain() = "}\n"

  private def endMain(fileName: String) = s"}\n// END of '$fileName.c'"
}