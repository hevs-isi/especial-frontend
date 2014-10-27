package hevs.especial.genenator

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.digital.DigitalOutput
import hevs.especial.generator.DotPipe
import hevs.especial.utils.Logger
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

  private val dot = new DotPipe().run(log) _

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
  def runDotTest(): Unit = {
    // Reset and run the DSL program
    ComponentManager.reset()
    log.info(s"Test '$progName' started.")

    test("Run DOT") {
      getDslCode
      dot(progName)

      checkErrors()
    }
  }
}