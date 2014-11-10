package hevs.especial.ports

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput}
import hevs.especial.dsl.components.{ComponentManager, Pin, uint1}
import hevs.especial.utils.PortInputShortCircuit
import org.scalatest.{FunSuite, Matchers}

abstract class PortCode {
  val cst1 = Constant(uint1(v = true))
  val btn1 = DigitalInput(Pin('C', 6))
  val led1 = DigitalOutput(Pin('C', 12))
}

class PortCode1 extends PortCode {
  cst1.out --> led1.in
}

class PortCode2 extends PortCode {
  // Two outputs to the same input = short circuit !
  cst1.out --> led1.in
  btn1.out --> led1.in
}


class PortTests extends FunSuite with Matchers {

  test("Input to output") {
    ComponentManager.reset()
    new PortCode1()
  }

  test("Input short circuit") {
    ComponentManager.reset()

    intercept[PortInputShortCircuit] {
      new PortCode2()
    }
    info("Short circuit detected")
  }
}
