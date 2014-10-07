package hevs.androiduino.dsl.generator

import java.io.File

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.fundamentals.hw_implemented
import hevs.androiduino.dsl.utils.OSUtils._
import hevs.androiduino.dsl.utils.{OSUtils, Version}

import scala.collection.mutable
import scala.sys.process._

/**
 * To generate the C code of a program, the `Resolver` class must be used to first resolve the graph,
 * and then generate the code for each connected components in the right order.
 */
object CodeGenerator extends Logging {

  private val cps = mutable.ListBuffer.empty[hw_implemented]

  def generateCodeFile(progName: String, fileName: String): String = {
    val code = generateCode(progName)
    // Create the file in the folder "output/dot/"
    val path = s"output/code/$fileName.c"
    info(s"Code generated to '$path'.")
    val file: RichFile = new File(path)
    file.write(code)

    // Call the AStyle conversion program
    info("Running Astyle.")
    OSUtils.getOsType match {
      case _: Windows => s"./lib/AStyle.exe --style=kr -Y $path".!!
      case _: Linux => s"./lib/astyle --style=kr -Y $path".!!
      case _ => error("OS not supported. Cannot run `astyle`.")
    }

    code // Return the non-formatted code
  }

  def generateCode(progName: String): String = {

    // Resolve the graph before generating the C code
    val resolve = Resolver.resolve()

    // Order the result by pass number (sort by key value)
    val ordered = resolve.toSeq.sortBy(_._1)

    // Save all components in the right order
    cps.clear()
    cps ++= ordered flatMap (x => x._2)


    if (hasWarnings) {
      info("Warnings found !")
      printWarnings()
    }

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

  /**
   * Check and display warnings.
   */
  def printWarnings() = checkWarnings() match {
    case Some(w) =>
      info(w)
    case _ =>
  }

  /**
   * Run some checks to detect warnings.
   * @return list of warnings or `None` if no warnings.
   */
  private def checkWarnings(): Option[String] = {

    // TODO check other warnings
    // FIXME check if an input is not connected

    val out = new StringBuilder
    val c = ComponentManager.findUnconnectedComponents
    if (c.nonEmpty) {
      out ++= s"WARN: ${c.size} unconnected component(s) found:"
      out ++= "\t- " + c.mkString("\n\t- ")
      Some(out.toString())
    }
    else
      None // No warnings
  }

  def preamble(progName: String) = {
    val ver = Version.getVersion
    "/*" + "\n" +
      " " + "*".*(60) + "\n" +
      s" Version $ver\n" +
      s" Code of '$progName.c' generated automatically.\n" +
      " " + "*".*(60) + "\n" +
      " */\n\n"
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

  /**
   * A program with warnings.
   * @return true if warnings found, false otherwise
   */
  def hasWarnings: Boolean = checkWarnings().isDefined

  /**
   * A program without warning.
   * @return true if no warnings found, false otherwise
   */
  def hasNoWarning = !hasWarnings
}