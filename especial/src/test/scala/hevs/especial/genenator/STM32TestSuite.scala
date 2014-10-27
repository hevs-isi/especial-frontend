package hevs.especial.genenator

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.digital.DigitalOutput
import hevs.especial.generator.{CodeChecker, DotPipe}
import hevs.especial.utils.{Logger, Settings}
import org.scalatest.FunSuite

/**
 * Base class used to run tests for the `STM32-103STK` board.
 */
abstract class STM32TestSuite extends FunSuite {

  /* I/O definition */

  /** Red led of the board. */
  protected val led1 = DigitalOutput(12)


  /* Pipeline */

  /** Logger used to report errors with the pipeline. */
  private val log = new Logger
  log.reset() // Reset before all tests

  private val dot = new DotPipe().run(log) _
  private val checker = new CodeChecker().run(log) _

  /** The name of the test is its class name. */
  private val progName = this.getClass.getSimpleName

  /**
   * The test fail if any error is reported to the logger.
   */
  private def checkErrors(): Unit = {
    if (log.hasErrors) {
      // assert(!log.hasErrors)
      fail("Errors reported to the Logger.")
    }
  }

  /**
   * The DSL program to run for this test.
   * @return the DSl program to execute
   */
  def getDslCode: Any

  /**
   * Run the DOT pipeline block and check if any errors occurs.
   */
  def runDotGeneratorTest(): Unit = {
    if (!Settings.PIPELINE_RUN_DOT)
      return // Test disabled

    // Reset and run the DSL program
    ComponentManager.reset()
    test("Dot generator") {
      log.info(s"Dot generator test for '$progName' started.")

      getDslCode
      dot(progName)

      checkErrors()
    }
  }

  /**
   * Run the code checker pipeline block
   * @param hasWarnings true if the DSL program has warnings
   */
  def runCodeCheckerTest(hasWarnings: Boolean = false): Unit = {
    if (!Settings.PIPELINE_RUN_CODE_CHECKER)
      return // Test disabled

    test("Code checker") {
      log.info(s"Code checker test for '$progName' started.")

      val warns = checker("")
      assert(log.hasWarnings == hasWarnings)

      // Not excepted result
      if (warns != hasWarnings) {
        if (hasWarnings)
          fail("Warnings found !")
        else
          fail("No warning found !")

      }
    }
  }
}