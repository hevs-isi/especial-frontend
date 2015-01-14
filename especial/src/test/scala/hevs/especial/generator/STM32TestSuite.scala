package hevs.especial.generator

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.target.stm32stk.Stm32stk
import hevs.especial.generator._
import hevs.especial.simulation.QemuLogger
import hevs.especial.utils.{Context, PortInputShortCircuit, PortTypeMismatch, Settings}
import org.scalatest.FunSuite

/**
 * Base class used to run tests for the `STM32-103STK` board.
 *
 * These tests are running automatically on the QEMU version. The logger can be enabled to trace event from the code
 * simulation on the QEMU side.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
abstract class STM32TestSuite extends FunSuite {

  /** Enable the logger in QEMU or not. */
  def isQemuLoggerEnabled: Boolean

  private var progExecuted = false

  /* Pipeline */

  // Create the context depending on the user parameter
  private val progName = this.getClass.getSimpleName
  private val ctx = new Context(progName, isQemuLoggerEnabled)

  def getContext: Context = ctx

  protected val optimizer = new CodeOptimizer()
  protected val resolver = new Resolver()


  private def executeProg(): Boolean = {
    if (progExecuted) {
      return checkErrors() // Execute the DSL code only once
    }

    // Reset. Delete all previous components.
    ComponentManager.reset()

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
      return true
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
   *
   * @param optimizedVersion true if the current program is optimized, false otherwise
   */
  def runDotGeneratorTest(optimizedVersion: Boolean = false): Unit = {
    if (!Settings.PIPELINE_RUN_DOT)
      return // Test disabled

    val suffix = if (optimizedVersion) Some("opt") else None
    test("Dot generator " + suffix.getOrElse("")) {
      ctx.log.info(s"Dot generator test for '$progName' started.")

      executeProg() // Run the DSL code

      new DotGenerator().run(ctx)(suffix) // Execute the DOT pipeline
      checkErrors()
    }
  }

  /**
   * Run the code checker pipeline block.
   * At the end, check if expected warnings have been found or not.
   *
   * @param hasWarnings `true` if the DSL program should have warnings
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

  /**
   * Optimize the current program.
   * At the end, check that no code warnings are reported.
   */
  def runCodeOptimizer(hasWarnings: Boolean = false): Unit = {
    if (!Settings.PIPELINE_RUN_CODE_OPTIMIZER)
      return // Test disabled

    test("Code optimizer") {
      ctx.log.info(s"Code optimizer test for '$progName' started.")

      executeProg() // Run the DSL code

      val res = optimizer.run(ctx)(Unit)
      assert(res, "Code optimizer not enabled !")
      assert(CodeChecker.hasWarnings == hasWarnings, "Warnings found after the optimizer !")
    }
  }


  def runCodeGenTest(compile: Boolean = true): Unit = {
    // Generate the code once the program is optimized.
    test("Resolver and code gen") {
      val res = compileCode(compile)
      ctx.log.info("Final result: " + res)
    }
  }

  /**
   * Execute and compile the specified test application.
   * @param compile compile the DSL application (default is true)
   * @return the pipeline result as a String
   */
  def compileCode(compile: Boolean = true): String = {
    ctx.log.info(s"Code generator for '$progName' started.")

    executeProg() // Run the DSL code

    val gen = new CodeGenerator()
    val formatter = new CodeFormatter()
    val compiler = new CodeCompiler()

    // The pipeline with or without the compile
    val pipe = if (compile) resolver -> gen -> formatter -> compiler else resolver -> gen -> formatter
    pipe.run(ctx)(Unit) // Run an return the pipeline result
  }
}