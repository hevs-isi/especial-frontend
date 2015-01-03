package hevs.especial.compiler

import hevs.especial.generator.VcdGenerator
import hevs.especial.simulation.{Events, Monitor}
import hevs.especial.utils.{Context, OSUtils, Settings}

// TODO: check this ?

/**
 * Path example from a program to the QEMU simulation.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class CompilerPathTest extends MonitorTest {

  test("Compiler path test") {

    val m = new Monitor()

    val runQemu = s"./${Settings.PATH_QEMU_STM32}/arm-softmmu/qemu-system-arm -M stm32-p103 -kernel " +
      "csrc/target-qemu/csrc.elf -serial null -monitor null -nographic"

    info("Start QEMU in a new process...")
    val qemuProcess = OSUtils.runInBackground(runQemu)
    Thread.sleep(1000)
    info("-----------------------------------------\n\n")

    m.waitForClient() match {
      case true => m
      case false =>
        fail("Timeout. Test aborted.")
    }

    // *********************************
    m.reader.waitForEvent(Events.MainStart)
    info("Program started.")
    m.writer.ackEvent(Events.MainStart)
    info("> MainStart ACK")

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

    if (pins.nonEmpty) {

      // generate the VCD file
      val gen = new VcdGenerator()
      val ctx = new Context("vcdTest", true)

      // Generate the VCD file using the pipeline block
      gen.run(ctx)(pins)

      // Check if errors have been reported or not
      assert(!ctx.log.hasWarnings)
      assert(!ctx.log.hasErrors)
    }
  }
}
