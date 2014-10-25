package hevs.androiduino.dsl.utils

import java.io.IOException
import scala.collection.mutable

/**
 * Utils class to known on which OS the program is running.
 */
object OSUtils {

  /**
   * Name of the OS. Can be `linux` or `windows 8` for instance.
   */
  val osName: String = System.getProperty("os.name").toLowerCase

  def isWindows = getOsType == Windows

  /**
   * Return the OS type or `Other` if unknown.
   * @return OS type
   */
  def getOsType: Os = {
    if (osName contains "windows")
      Windows
    else if (osName contains "linux")
      Linux
    else
      Other
  }

  def isLinux = getOsType == Linux

  def isOther = getOsType == Other

  /**
   * Runs command and return its result. Thrown an `IOException` if the command fail.
   * @throws IOException if the command fails
   * @param cmd the command to run
   * @return the result of the command
   */
  def runWithResult(cmd: String): String = {
    val (code, res) = runWithCodeResult(cmd)
    if (code != 0)
      throw new IOException(res)
    else
      res
  }

  /**
   * Run a command and return the exit code and the result. If the command cannot be executed,
   * the error code will be `-1` (no exception is thrown).
   * @param cmd the command to run
   * @return the result and the exit code of the command. `0` if success.
   */
  def runWithCodeResult(cmd: String): (Int, String) = {
    import scala.sys.process._
    val out = mutable.ListBuffer.empty[String]
    val logger = ProcessLogger(l => out append l) // Add lines
    try {
      val code = cmd ! logger // Execute
      (code, out.mkString("\n")) // Result lines with exit code
    }
    catch {
      case e: Exception => (-1, e.getMessage)
    }
  }

  sealed abstract class Os

  case object Windows extends Os

  case object Linux extends Os

  case object Other extends Os

}