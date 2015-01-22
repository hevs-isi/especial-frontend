package hevs.especial.apps

import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.generator.STM32TestSuite
import hevs.especial.simulation.{Events, Monitor, MonitorTest, MonitorWriter}
import hevs.especial.utils.OSUtils

/**
 * Test case used to simulate the [[hevs.especial.apps.Majority]] application.
 *
 * QEMU events are used to monitor the MCU code execution in QEMU. This Scala test case control the execution of the
 * code inside QEMU. When a loop iteration is terminated, input values are modified and the execution restart for a
 * new loop iteration with new input values. After a number of iterations, the QEMU program is stopped and the VCD
 * result file is generated.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class MajorityInputSimulation extends MonitorTest {

  /** Input buttons values (all possible combinations, see truth table). */
  private val inValues = Map(
    3 -> "0,1,0,1,0,1,0,1", // Input C (btn3)
    2 -> "0,0,1,1,0,0,1,1", // Input B (btn2)
    1 -> "0,0,0,0,1,1,1,1"  // Input A (btn1)
  )

  /** Expected output values. */
  private val outValues = Array(
    1 -> "0,0,0,1,0,1,1,1"  // Output O (led1) = majority function
  )

  private def updateInputValues(w: MonitorWriter, tick: Int): Unit = {
    inValues.foreach(input => {
      val btId = input._1
      val curVal = input._2.split(",")(tick)
      info(s"Set input '$btId' to '$curVal'.")
      w.setButtonInputValue(btId, curVal == "1") // Send the input boolean value
    })
  }

  /**
   * Simulate a test program in QEMU.
   * Monitor read and writer are used to get and set values from QEMU.
   *
   * @param code the test code to simulate
   */
  override def simulateCode(code: STM32TestSuite) = {
    val m = new Monitor()

    info("Launching QEMU...\n")
    val qemuProcess = OSUtils.runInBackground(runQemu)
    Thread.sleep(1000)
    info(" > QEMU started.")

    if(!m.waitForClient())
      fail("Timeout. Test aborted.")

    // *********************************
    var countTick = 0
    while (countTick < 8) {
      m.reader.waitForEvent(Events.LoopTick)

      // Set input value
      updateInputValues(m.writer, countTick)

      m.writer.ackEvent(Events.LoopTick)
      info(s"> Loop iteration $countTick ended.")
      countTick += 1
    }

    // Wait for the last loop iteration
    m.reader.waitForEvent(Events.LoopTick)
    info(s"> Program terminated after $countTick iterations.")

    // Print result values
    pins = m.reader.getOutputValues
    printOutputValues(pins)

    // Check output values
    val expectedValues = outValues.head._2.split(",").map(x => x.toInt) // Expected values from truth table
    val realValues = pins.get(Stm32stkIO.led4_pin).get                  // Real values read from QEMU
    val success = expectedValues sameElements realValues
    if (!success)
      fail("Error in the majority function !")
    // *********************************

    // Close QEMU
    info("Kill QEMU.")
    disconnect(m)
    qemuProcess.destroy()
  }


  /** DSL program under test. */
  runTests(new Majority())
}
