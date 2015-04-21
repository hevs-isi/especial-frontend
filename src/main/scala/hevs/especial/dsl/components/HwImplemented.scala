package hevs.especial.dsl.components

trait HwImplemented {

  /**
   * Include (header) files on the top of the generated code.
   * @return the list of path/file name to include with its extension
   */
  def getIncludeCode: Seq[String] = Nil

  // Code inserted in the global section to declare global variables, constants, etc.
  def getGlobalCode: Option[String] = None

  // Code inserted in for function definitions (pre-main)
  def getFunctionsDefinitions: Option[String] = None

  // Code inserted to init the component (executed once)
  def getInitCode: Option[String] = None

  // Code executed once before the while loop
  def getBeginOfMainAfterInit: Option[String] = None

  // Code inserted in the main loop
  def getLoopableCode: Option[String] = None

  // Final code after the main loop
  def getExitCode: Option[String] = None
}