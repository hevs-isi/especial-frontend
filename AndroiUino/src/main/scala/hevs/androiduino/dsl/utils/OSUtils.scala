package hevs.androiduino.dsl.utils

/**
 * Utils class to known on which OS the program is running.
 */
object OSUtils {

  sealed abstract class Os

  case object Windows extends Os

  case object Linux extends Os

  case object Other extends Os

  /**
   * Name of the OS. Can be `linux` or `windows 8` for instance.
   */
  val osName: String = System.getProperty("os.name").toLowerCase

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

  def isWindows = getOsType == Windows

  def isLinux = getOsType == Linux

  def isOther = getOsType == Other

  /**
   * Runs command and return its result. Thrown an `IOException` if the command fail.
   * @throws IOException if the command fails
   * @param cmd the command to run
   * @return the result of the command
   */
  def runWithResult(cmd: String): String = {
    import scala.sys.process._
    val out = new StringBuilder
    val logger = ProcessLogger(l => out append l)
    cmd ! logger // Execute
    out.result() // Result
  }

  /**
   * Run a command and check if it is executed without error. If an error occurred, return `false` (without exception).
   * @param cmd the command to run
   * @return true if the command was executed without error, false otherwise
   */
  def runWithBooleanResult(cmd: String): (Boolean, String) = {
    var out = ""
    try
      out = runWithResult(cmd)
    catch {
      case _: Exception => return (false, "")
    }
    (true, out)
  }
}