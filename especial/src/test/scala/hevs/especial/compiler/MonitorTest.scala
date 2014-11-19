package hevs.especial.compiler

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.Pin
import hevs.especial.generator.VcdGenerator
import hevs.especial.simulation._
import hevs.especial.utils.Context
import org.scalatest.FunSuite


class MonitorTest extends FunSuite with Logging {

  protected var pins = Map.empty[Pin, Seq[Int]]

  protected def waitForClient(): Monitor = {
    info("Monitor test. Wait for client...")

    // Start the monitor and wait for a client
    val m = new Monitor()
    m.waitForClient() match {
      case true => m
      case false =>
        fail("Timeout. Test aborted.")
    }
  }

  protected def disconnect(ms: Monitor) = ms.close()

  /*test("Monitor code Sch1") {
    val m = startTest()

    m.waitForLastEvent(LoopExit)
    info(s"Event $LoopExit received. The C program is finished.")

    // Print output values
    v = m.getOutputValues
    printOutputValues(v)

    // Check values for one specific output
    val ledValues = v(Stm32stk.pin_led)
    assert(ledValues.length == 11)
    assert(ledValues == Seq(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1))

    // Count the number of the while loop ticks
    val ticks = m.getEvents.count(_ == LoopTick)
    assert(ticks == 10)
  }*/

  // Format output values and print them
  protected def printOutputValues(values: Map[Pin, Seq[Int]]) = {
    values foreach (x => info(s"Pin ${x._1} has ${x._2.length} values: ${x._2.mkString("-")}"))
  }

  /* test("Monitor for 'Sch3'") {
    val m = waitForClient()

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
  } */

  /*test("VCD generator") {
    val gen = new VcdGenerator()
    val ctx = new Context("Sch3Code", true)

    // Generate the VCD file using the pipeline block
    gen.run(ctx)(pins)

    // Check if errors have been reported or not
    assert(!ctx.log.hasWarnings)
    assert(!ctx.log.hasErrors)
  }*/
}
