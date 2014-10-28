package hevs.especial.generator

import java.io.File

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.fundamentals.hw_implemented
import hevs.especial.utils.OSUtils.{Linux, Windows}
import hevs.especial.utils._

import scala.collection.mutable

/**
 * To generate the C code of a program, the `Resolver` class must be used to first resolve the graph,
 * and then generate the code for each connected components in the right order.
 */
object CodeGenerator extends Logging {

  private val cps = mutable.ListBuffer.empty[hw_implemented]

  def generateCodeFile(progName: String, fileName: String): String = {
    // FIXME: use a pipeline for this

    val code = generateCode(progName)
    // Create the file in the folder "output/dot/"
    val path = s"output/code/$fileName.c"
    info(s"Code generated to '$path'.")
    val file: RichFile = new File(path)
    file.write(code)

    // Call the AStyle conversion program
    var run = "./third_party/astyle/%s"
    OSUtils.getOsType match {
      case Windows =>
        run = run.format("astyle.exe")
      case Linux =>
        run = run.format("astyle")
      case _ => throw new OsNotSupported("Cannot run astyle.")
    }

    val valid = OSUtils.runWithCodeResult(run + " -V")
    if (valid._1 == 0) {
      info(s"Running '${valid._2}'.")
      OSUtils.runWithResult(run + s" --style=kr -Y $path")
    }
    else
      error("Unable to run AStyle !")

    code // Return the non-formatted code
  }

  def generateCode(progName: String): String = {

    // Clear the object state
    cps.clear()

    // Resolve the graph before generating the C code
    val resolve = new Resolver().run(new Logger)("")

    // Order the result by pass number (sort by key value)
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

  def preamble(progName: String) = {
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

  def preInit() = "void init() {\n"

  def postInit() = "}\n\n"

  def beginMain() = "int main() {\n"

  def beginLoopMain() = "while(1){\n"

  def endLoopMain() = "}\n"

  def endMain(fileName: String) = s"}\n// END of '$fileName.c'"
}