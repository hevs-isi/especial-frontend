package hevs.especial.genenator

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.target.stm32stk.Stm32stk
import hevs.especial.generator._
import hevs.especial.simulation.QemuLogger
import hevs.especial.utils.{Context, Settings}
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


  private def executeProg(): Unit = {
    if (progExecuted) {
      ctx.log.info("Program ready.")
      return // Execute once only
    }

    ComponentManager.reset() // Delete all previous components

    // Add the target as a component with the logger or not
    ctx.isQemuLoggerEnabled match {
      case true =>
        new Stm32stk with QemuLogger
        ctx.log.info("QEMU logger is enabled.")
      case _ =>
        new Stm32stk
        ctx.log.info("QEMU logger is disabled.")
    }

    // Execute the DSL program
    getDslCode
    progExecuted = true
    ctx.log.info(s"Program '$progName' executed.")
  }

  /**
   * The test fail if any error is reported to the logger.
   */
  private def checkErrors(): Unit = {
    if (ctx.log.hasErrors) {
      // assert(!log.hasErrors)
      fail("Errors reported to the Logger.") // Test failed
    }
  }

  /**
   * The DSL program to run for this test.
   * @return the DSl program to execute
   */
  def getDslCode: Unit

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
    if (!Settings.PIPELINE_RUN_CODE_CHECKER)
      return // Test disabled

    test("Code checker") {
      ctx.log.info(s"Code checker test for '$progName' started.")

      executeProg() // Run the DSL code

      // Run the code checker
      val warns = new CodeChecker().run(ctx)(Unit)
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