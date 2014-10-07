package hevs.androiduino.dsl.generator

import java.io.File

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.utils.OSUtils._
import hevs.androiduino.dsl.utils.{OSUtils, Version}

import scala.sys.process._

object CodeGenerator extends Logging {

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

    printWarnings()

    val result =
      preamble(progName) +
        ComponentManager.generateGlobalCode +
        ComponentManager.generateFunctionsCode +
        preInit() +
        ComponentManager.generateInitCode +
        postInit() +
        beginMain() +
        ComponentManager.generateBeginMainCode +
        beginLoopMain() +
        ComponentManager.generateLoopingCode +
        endLoopMain() +
        endMain(progName)

    result
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

  def preInit() = "void init() {\n"

  def postInit() = "}\n\n"

  def beginMain() = "int main() {\n"

  def beginLoopMain() = "while(1){\n"

  def endLoopMain() = "}\n"

  def endMain(fileName: String) = s"}\n// END of '$fileName.c'"

  /**
   * A program without warning.
   * @return true if no warnings found, false otherwise
   */
  def hasNoWarning = !hasWarnings
  
  /**
   * A program with warnings.
   * @return true if warnings found, false otherwise
   */
  def hasWarnings: Boolean = checkWarnings().isDefined
}