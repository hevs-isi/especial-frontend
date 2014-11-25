package hevs.especial.genenator

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.target.stm32stk.Stm32stk
import hevs.especial.generator._
import hevs.especial.simulation.QemuLogger
import hevs.especial.utils.{PortInputShortCircuit, PortTypeMismatch, Context, Settings}
import org.scalatest.FunSuite

/**
 * Base class used to run tests for the `STM32-103STK` board.
 * These tests are running automatically on the QEMU version. The logger is enabled to trace event from the code.
 */
abstract class STM32TestSuite extends FunSuite {

  /** Enable the logger in QEMU or not. */
  def isQemuLoggerEnabled: Boolean

  private var progExecuted = false

  /* Pipeline */

  /** Pipeline context */
  private val progName = this.getClass.getSimpleName

  // Create the context depending on the user parameter
  private val ctx = new Context(progName, isQemuLoggerEnabled)


  private def executeProg(): Boolean = {
    if (progExecuted) {
      return checkErrors() // Execute the DSL code only once
    }

    ComponentManager.reset() // Delete all previous components

    // Add the target as a component with the logger or not
    ctx.isQemuLoggerEnabled match {
      case true =>
        new Stm32stk with QemuLogger
        ctx.log.trace("QEMU logger is enabled.")
      case _ =>
        new Stm32stk
        ctx.log.trace("QEMU logger is disabled.")
    }

    // Execute the DSL program
    try {
      runDslCode()
      ctx.log.info(s"Program '$progName' executed.")
    }
    catch {
      case e: Exception =>
        ctx.log.error(e.getMessage)
        ctx.log.error(s"Error when running the '$progName' program.")
    }

    progExecuted = true
    checkErrors()
  }

  /**
   * The test fail if any error is reported to the logger.
   * @return `true` if errors found, `false` otherwise
   */
  private def checkErrors(): Boolean = {
    if (ctx.log.hasErrors) {
      fail("Errors reported to the Logger.") // Test failed
      true
    }
    false
  }

  /**
   * The DSL program to run for this test.
   *
   * Can thrown Exception at runtime if ports types mismatch.
   * @throws PortTypeMismatch ports types mismatch. Connection error
   * @throws PortInputShortCircuit more than one output is connected to the same input
   * @return the DSl program to execute
   */
  @throws(classOf[PortTypeMismatch])
  @throws(classOf[PortInputShortCircuit])
  def runDslCode(): Unit

  /**
   * Run the DOT pipeline block and check if any errors occurs.
   */
  def runDotGeneratorTest(): Unit = {
    if (!Settings.PIPELINE_RUN_DOT)
      return // Test disabled

    test("Dot generator") {
      ctx.log.info(s"Dot generator test for '$progName' started.")

      executeProg() // Run the DSL code

      new DotPipe().run(ctx)(Unit) // Execute the DOT pipeline
      checkErrors()
    }
  }

  /**
   * Run the code checker pipeline block
   * @param hasWarnings true if the DSL program has warnings
   */
  def runCodeCheckerTest(hasWarnings: Boolean = false): Unit = {
    if (!Settings.PIPELINE_RUN_CODE_OPTIMIZER)
      return // Test disabled

    test("Code checker") {
      ctx.log.info(s"Code checker test for '$progName' started.")

      executeProg() // Run the DSL code

      // Run the code checker
      val warns = new CodeOptimizer().run(ctx)(Unit)
      assert(ctx.log.hasWarnings == hasWarnings)

      // Not excepted result
      if (warns != hasWarnings) {
        if (hasWarnings)
          fail("Warnings found !")
        else
          fail("No warning found !")
      }
    }
  }

  def runCodeGenTest(compile: Boolean = true): Unit = {
    test("Resolver and code gen") {
      ctx.log.info(s"Code generator for '$progName' started.")

      executeProg()

      val resolve = new Resolver()
      val gen = new CodeGenerator()
      val formatter = new CodeFormatter()
      val compiler = new CodeCompiler()

      // The pipeline with or without the compile
      val pipe = if (compile) resolve -> gen -> formatter -> compiler else resolve -> gen -> formatter

      val res = pipe.run(ctx)(Unit)
      ctx.log.info("Final result: " + res)
    }
  }
}