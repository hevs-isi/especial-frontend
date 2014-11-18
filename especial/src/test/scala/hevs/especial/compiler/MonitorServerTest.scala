package hevs.especial.compiler

import grizzled.slf4j.Logging
import hevs.especial.dsl.components.Pin
import hevs.especial.simulation._
import org.scalatest.FunSuite

class MonitorServerTest extends FunSuite with Logging {

  // Output values
  var v: Map[Pin, Seq[Int]] = Map.empty[Pin, Seq[Int]]

  private def waitForClient(): (MonitorReader, MonitorWriter) = {
    info("Monitor test. Wait for client...")

    // Start the monitor and wait for a client
    val ms = new MonitorServer()
    val m = ms.waitForClient()
    m
  }

  private def disconnect(m: (MonitorReader, MonitorWriter)): Unit = {
    // Close the TCP servers at the end
    m._1.disconnect()
    m._2.disconnect()
  }

  // Format output values and print them
  private def printOutputValues(values: Map[Pin, Seq[Int]]) = {
    values foreach (x => info(s"Pin ${x._1} has ${x._2.length} values: ${x._2.mkString("-")}"))
  }

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

  test("Monitor code Sch3") {
    val m = waitForClient()
    val reader = m._1
    val writer = m._2

    reader.waitForEvent(Events.MainStart)
    info("Program started.")
    writer.ackEvent()
    info("> MainStart ACK")

    var countTick = 0
    while (countTick < 5) {
      reader.waitForEvent(Events.LoopTick)
      info("LoopTick event: " + countTick)
      countTick += 1

      writer.ackEvent()
      info("> LoopTick ACK")
    }
    Thread.sleep(100)
    info(s"${countTick + 1} loop ticks. Exit.")

    // Print output values
    v = reader.getOutputValues
    printOutputValues(v)

    disconnect(m)
  }
}
