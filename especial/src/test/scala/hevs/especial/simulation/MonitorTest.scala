package hevs.especial.simulation

import java.io.IOException

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.Pin
import hevs.especial.generator.{STM32TestSuite, VcdGenerator}
import hevs.especial.utils.{OSUtils, Settings}
import org.scalatest.FunSuite

/**
 * Base test class used to create a [[Monitor]] TCP server.
 *
 * Different test are available to simulate a program using QEMU.
 *
 * 1) The test program is compiled and the code is generated.
 * 2) The program is simulated using QEMU. 6 loops ticks are executed.
 * 3) Output values are exported to a VCD file.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class MonitorTest extends FunSuite with Logging {

  /** Save the sequence of output values. */
  protected var pins = Map.empty[Pin, Seq[Int]]

  /**
   * Start a [[Monitor]] and wait for a client connection.
   * Wait until QEMU is connected to the monitor. Thrown an error after a timeout if no client are connected.
   * This will automatically terminates the current test with an error.
   *
   * @return the create monitor to communicate with QEMU
   */
  @throws[IOException]("Connection timeout.")
  protected def waitForClient(): Monitor = {
    info("Monitor test. Wait for client...")

    // Start the monitor and wait for a client
    val m = new Monitor()
    m.waitForClient() match {
      case true => m
      case false => throw new IOException("Timeout. Test aborted.")
    }
  }

  /**
   * Disconnect the monitor.
   * Close the TCP/IP connection with QEMU when the simulation test is finished.
   * This will force to stop QEMU.
   * @param m the monitor to close
   */
  protected def disconnect(m: Monitor) = m.close()

  /**
   * Format and print the output pin values.
   * @param values sequence of pin values to print
   */
  protected def printOutputValues(values: Map[Pin, Seq[Int]]) = {
    // Output example:
    // Pin 'C#03' has 07 values:	0-1-0-1-0-1-0
    // Pin 'C#04' has 07 values:	0-0-0-0-0-0-0
    values foreach (x => {
      val size = "%02d" format x._2.length
      info(s"Pin '${x._1}' has $size values:\t${x._2.mkString("-")}")
    })
  }

  /**
   * First compile a simple code to simulate in QEMU
   * @param code the test code to compile
   */
  protected def compileCode(code: STM32TestSuite): Unit = code.compileCode()

  /**
   * Simulate a test program in QEMU.
   * @param code the test code to simulate
   */
  protected def simulateCode(code: STM32TestSuite) = {
    val m = new Monitor()

    val runQemu = s"./${Settings.PATH_QEMU_STM32}/arm-softmmu/qemu-system-arm -M stm32-p103 -kernel " +
      "csrc/target-qemu/csrc.elf -serial null -monitor null -nographic"

    info("Start QEMU in a new process...\n\n")
    val qemuProcess = OSUtils.runInBackground(runQemu)
    Thread.sleep(1000)
    info(" > QEMU started.")

    m.waitForClient() match {
      case false => fail("Timeout. Test aborted.")
      case _ =>
    }

    // *********************************
    m.reader.waitForEvent(Events.LoopStart)
    info("Program ready. Loop start received.")
    m.writer.ackEvent(Events.MainStart)
    info("> While loop started. ACK sent.")

    var countTick = 0
    while (countTick < 5) {
      m.reader.waitForEvent(Events.LoopTick)
      info("LoopTick event: " + countTick)
      countTick += 1

      m.writer.ackEvent(Events.LoopTick)
      info("> LoopTick ACK")
    }
    Thread.sleep(100)
    info(s"${countTick + 1} loop ticks. Exit.")

    // Print output values
    pins = m.reader.getOutputValues
    printOutputValues(pins)

    disconnect(m)
    // *********************************

    // Close QEMU
    info("Kill QEMU.")
    qemuProcess.destroy()
  }

  /**
   * Export pin output values to a VCD file, when the code has been simulated in QEMU.
   * @param code the simulated code (uses its context to print infos)
   */
  protected def exportVcd(code: STM32TestSuite) = {
    val ctx = code.getContext

    if (pins.nonEmpty) {
      // Generate the VCD file using the pipeline block
      val vcdGen = new VcdGenerator().run(ctx)(pins)
      assert(vcdGen, "Unable to generate the VCD file !")

      // Check if errors have been reported or not
      assert(!ctx.log.hasWarnings)
      assert(!ctx.log.hasErrors)
    }
    else
      fail(s"No VCD to generate for '${ctx.progName}'.")
  }

  /**
   * Run compilation, simulation and VCD export tests.
   * @param code the program to test
   * @return `true` if successful, `false` otherwise
   */
  protected def runTests(code: STM32TestSuite): Boolean = {
    val progName = code.getContext.progName

    // Code compilation
    test(s"$progName compilation") {
      compileCode(code)
      if (code.getContext.log.hasErrors) {
        fail()
        return false
      }
    }

    // QEMU simulation test
    test(s"$progName QEMU") {
      simulateCode(code)
    }

    // VCD export test
    test(s"$progName VCD") {
      exportVcd(code)
    }

    true
  }
}
