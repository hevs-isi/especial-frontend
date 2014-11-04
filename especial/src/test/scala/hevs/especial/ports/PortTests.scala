package hevs.especial.ports

import hevs.especial.dsl.components.{uint1, ComponentManager}
import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.especial.utils.PortInputShortCircuit
import org.scalatest.{FunSuite, Matchers}

abstract class PortCode {
  val cst1 = Constant(uint1(v = true))
  val btn1 = DigitalInput(4)
  val led1 = DigitalOutput(7)
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
