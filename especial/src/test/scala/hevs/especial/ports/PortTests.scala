package hevs.especial.ports

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}
import hevs.especial.dsl.components.{uint8, ComponentManager, bool}
import hevs.especial.utils.{PortTypeMismatch, PortInputShortCircuit}
import org.scalatest.{FunSuite, Matchers}

/**
 * Test input and output port connections.
 *
 * Connection types must be the same. An output can be connected to different inputs,
 * but an input can only have one connection, or a `PortInputShortCircuit` exception is thrown.
 */
class PortTests extends FunSuite with Matchers {

  abstract class PortCode {
    // Inputs
    val cst1 = Constant(bool(v = true))
    val cst2 = Constant(uint8(128))
    val btn1 = DigitalInput(Stm32stk.pin_btn)

    // Output
    val led1 = DigitalOutput(Stm32stk.pin_led)
  }

  class PortCode1 extends PortCode {
    // Valid
    cst1.out --> led1.in
  }

  class PortCode2 extends PortCode {
    // Two outputs to the same input (led1) = short circuit !
    cst1.out --> led1.in
    btn1.out --> led1.in
  }

  class PortCode3 extends PortCode {
    // Types mismatch: uint8 --> bool
    // Thrown an exception at runtime
    cst2.out --> led1.in
  }

  test("Input to output") {
    ComponentManager.reset()
    new PortCode1() // Connections are ok
    info("Connections are ok.")
  }

  test("Input short circuit") {
    ComponentManager.reset()

    val e = intercept[PortInputShortCircuit] {
      new PortCode2() // Short circuit detected
    }

    // Short circuit !
    // The input 'in' of Cmp[2] 'DigitalOutput' is already connected.
    info(e.getMessage) // Print exception message
  }

  test("Ports type mismatch") {
    ComponentManager.reset()

    val e = intercept[PortTypeMismatch] {
      new PortCode3() // Short circuit detected
    }

    info(e.getMessage) // Print exception message
  }
}
