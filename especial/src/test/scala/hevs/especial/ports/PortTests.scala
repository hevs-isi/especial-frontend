package hevs.especial.ports

import hevs.especial.dsl.components.core.{Mux2, Constant}
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, PwmOutput, Stm32stk}
import hevs.especial.dsl.components.{ComponentManager, Pin, bool, uint8}
import hevs.especial.utils.{CycleException, IoTypeMismatch, PortInputShortCircuit, PortTypeMismatch}
import org.scalatest.{FunSuite, Matchers}

/**
 * Test cases for input and output ports.
 *
 * Connection types must be the same, or a `PortTypeMismatch` exception is thrown.
 * An output can be connected to different inputs, but an input can only have one connection,
 * or a `PortInputShortCircuit` exception is thrown.
 *
 * - Check if an input is connected once only (short circuits).
 * - Check if ports type are the same. If not, they cannot be connected.
 * - Check if components can be created anonymously using the same pin. Only one component should be created.
 * - Check short circuits with anonymous components.
 * - Check if an input is used with the same type only.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class PortTests extends FunSuite with Matchers {

  abstract class PortCode {
    // Inputs
    val cst1 = Constant(bool(v = true))
    val cst2 = Constant(uint8(128))
    val btn1 = DigitalInput(Stm32stk.btn0_pin)

    // Output
    val led1 = DigitalOutput(Stm32stk.led0_pin)
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
    // This is also a short circuit.
    // The input is already connected, event if this is exactly the same connection. See issue #4
    cst1.out --> led1.in
    cst1.out --> led1.in
  }

  class PortCode4 extends PortCode {
    // This is also a short circuit.
    // Use anonymous components
    btn1.out --> DigitalOutput(Pin('A', 5)).in
    btn1.out --> DigitalOutput(Pin('A', 5)).in
  }

  class PortCode5 extends PortCode {
    // Types mismatch: uint8 --> bool
    // Thrown an exception at runtime
    cst2.out --> led1.in
  }

  class PortCode6 {
    val led1 = DigitalOutput(Stm32stk.led0_pin)
    val led2 = DigitalOutput(Pin('B', 6))

    // Should have exactly 3 components, not 4 !
    // Also work if used with a variable, see issue #8
    DigitalInput(Pin('A', 1)).out --> led1.in
    DigitalInput(Pin('A', 1)).out --> led2.in
  }

  class PortCode7 {
    // Thrown an error when the same I/O is used with different types
    val pin = Pin('A', 1)
    val out1 = DigitalOutput(pin)
    val out2 = PwmOutput(pin)
  }

  class PortCode8 {
    // Throw an exception when a cycle is found in the graph
    val cst1 = Constant(bool(v = true)).out
    val led1 = DigitalOutput(Stm32stk.led0_pin).in

    val mux = Mux2[bool]()
    cst1 --> mux.in2
    mux.out --> led1

    // This connection add a cycle in the graph. This is not permitted. Must be a DAG !
    mux.out --> mux.in1
  }


  test("Input to output") {
    ComponentManager.reset()
    new PortCode1()
    info("Connections are ok." + "\n--")
  }

  test("Input short circuit") {
    ComponentManager.reset()

    // Short circuits detection
    val e1 = intercept[PortInputShortCircuit] {
      new PortCode2()
    }
    // Short circuit !
    // The input 'in' of Cmp[3] 'DigitalOutput' is already connected.
    info(e1.getMessage + "\n--")

    val e2 = intercept[PortInputShortCircuit] {
      new PortCode3()
    }

    // Short circuit !
    // The input 'in' of Cmp[3] 'DigitalOutput' is already connected.
    info(e2.getMessage + "\n--")
  }

  test("Ports type mismatch") {
    ComponentManager.reset()

    val e = intercept[PortTypeMismatch] {
      new PortCode5() // Short circuit detected
    }

    // Ports types mismatch. Connection error !
    // Cannot connect the output `out` (type `uint8`) of Cmp[1] 'Constant'
    // to the input `in` (type `bool`) of Cmp[3] 'DigitalOutput'.
    info(e.getMessage + "\n--")
  }

  test("Anonymous short circuit") {
    ComponentManager.reset()

    val e = intercept[PortInputShortCircuit] {
      new PortCode4() // Short circuit detected
    }

    // Short circuit !
    // The input 'in' of Cmp[4] 'DigitalOutput' is already connected.
    info(e.getMessage + "\n--")
  }

  test("Anonymous input") {
    ComponentManager.reset()
    new PortCode6()
    assert(ComponentManager.numberOfNodes == 3)
  }

  test("Different IO types") {
    ComponentManager.reset()

    val e = intercept[IoTypeMismatch] {
      new PortCode7() // Type error detected
    }

    // IO already used !
    // The component Cmp[2] 'DigitalOutput' is already used as 'DigitalOutput'.
    // Cannot be used as 'PwmOutput'.
    info(e.getMessage + "\n--")
  }

  test("Find graph cycle") {
    ComponentManager.reset()

    val e = intercept[CycleException] {
      new PortCode8() // Cycle detected
    }

    // Cycle found in the graph. This is not permitted. The graph must be a DAG !
    // Addition refused. Wire error: OutputPort[2] 'out' of Cmp[02] 'Mux2' --> InputPort[0] 'in1' of Cmp[02] 'Mux2'
    info(e.getMessage + "\n--")
  }
}
