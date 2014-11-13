package hevs.especial.compiler

import hevs.especial.dsl.components.Pin
import hevs.especial.dsl.components.target.stm32stk.Stm32stk
import hevs.especial.simulation._
import org.scalatest.FunSuite

class MonitorServerTest extends FunSuite {

  test("Monitor code Sch1") {

    // Start the monitor and wait for a client
    val ms = new MonitorServer()
    info("Start monitor. Wait for client...")
    val m = ms.waitForClient()

    // ** The C program is running on QEMU. For the end event. **

    m.waitForLastEvent(LoopExit)
    info(s"Event $LoopExit received. The C program is finished.")

    // Print output values
    val v = m.getOutputValues
    printOutputValues(v)

    // Check values for one specific output
    val ledValues = v(Stm32stk.p_led)
    assert(ledValues.length == 11)
    assert(ledValues == Seq(0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1))

    // Count the number of the while loop ticks
    val ticks = m.getEvents.count(_ == LoopTick)
    assert(ticks == 10)

    // Close the TCP server at the end
    m.disconnect()
    ms.close()
  }

  // Format output values and print them
  private def printOutputValues(values: Map[Pin, Seq[Int]]) = {
    values foreach (x => info(s"Pin ${x._1} has ${x._2.length} values: ${x._2.mkString("-")}"))
  }
}
