package hevs.especial.ports

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}
import hevs.especial.dsl.components.{ComponentManager, bool}
import hevs.especial.utils.PortInputShortCircuit
import org.scalatest.{FunSuite, Matchers}

class PortTests extends FunSuite with Matchers {

  abstract class PortCode {
    val cst1 = Constant(bool(v = true))
    val btn1 = DigitalInput(Stm32stk.pin_btn)
    val led1 = DigitalOutput(Stm32stk.pin_led)
  }

  class PortCode1 extends PortCode {
    cst1.out --> led1.in
  }

  class PortCode2 extends PortCode {
    // Two outputs to the same input (led1) = short circuit !
    cst1.out --> led1.in
    btn1.out --> led1.in
  }


  test("Input to output") {
    ComponentManager.reset()
    new PortCode1() // Connections are fine
    info("Connections are Ok.")
  }

  test("Input short circuit") {
    ComponentManager.reset()

    intercept[PortInputShortCircuit] {
      new PortCode2() // Short circuit detected
    }
    info("Short circuit detected.")
  }
}
