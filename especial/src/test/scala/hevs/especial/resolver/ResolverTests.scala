package hevs.especial.resolver

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.core.logic.And2
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput}
import hevs.especial.dsl.components.{ComponentManager, Pin, uint1}
import hevs.especial.generator.CodeChecker

class ResolverCode1 {
  // Nothing to do, no inputs
  val btn1 = DigitalInput(Pin('C', 6))
}

// TODO: centraliser toutes ces applications et les utiliser pour tous les test, pas faire des cas particulier...

// 1 pass, 1 unconnected
class ResolverCode2 {
  val cst1 = Constant(uint1(false))
  val btn1 = DigitalInput(Pin('C', 6))
  val led1 = DigitalOutput(Pin('C', 12))
  cst1.out --> led1.in
}

// 3 passes without warning
class ResolverCode3 {
  val cst1 = Constant(uint1(false))
  val btn1 = DigitalInput(Pin('C', 6))
  val led1 = DigitalOutput(Pin('C', 12))
  val and1 = And2()

  cst1.out --> and1.in1
  btn1.out --> and1.in2
  and1.out --> led1.in
}

// 3 passes without warning
class ResolverCode4 {
  // Same as ResolverCode3 in a different order
  val led1 = DigitalOutput(Pin('C', 12))
  val cst1 = Constant(uint1(false))
  val and1 = And2()
  val btn1 = DigitalInput(Pin('C', 6))

  and1.out --> led1.in
  btn1.out --> and1.in2
  cst1.out --> and1.in1
}

class ResolverCode5 {
  val cst1 = Constant(uint1(false))
  val and1 = And2()
  val and2 = And2()
  val and3 = And2()
  val cst2 = Constant(uint1(false))
  val led1 = DigitalOutput(Pin('C', 12))

  cst1.out --> and1.in1
  and1.out --> and2.in2
  and2.out --> and3.in1
  cst2.out --> and3.in2
  and3.out --> led1.in
}

/**
 * Test specification for the `Resolver` class. Contains also some tests for the `ComponentManager`.
 */
class ResolverTest extends ResolverTestSpec {

  test("No components") {
    ComponentManager.reset()
    val res = testResolver()

    assert(res.isEmpty) // No connected components
    assert(r.getNumberOfPasses == 0)
  }

  test("1 unconnected component") {
    ComponentManager.reset()
    new ResolverCode1()
    val res = testResolver()

    assert(CodeChecker.hasWarnings)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 0)
    assert(r.getNumberOfPasses == 0) // Optimized. No connected components
    assert(res.isEmpty) // Nothing to resolve
  }

  test("1 wire with 1 unconnected component") {
    ComponentManager.reset()
    val c = new ResolverCode2()
    val res = testResolver()

    assert(CodeChecker.hasWarnings)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 2)
    assert(r.getNumberOfPasses == 2)
    assert(res.size == 2)
    assert(res(0) === Set(c.cst1))
    assert(res(1) === Set(c.led1))
  }

  test("3 passes without warning") {
    ComponentManager.reset()
    val c = new ResolverCode3()
    val res = testResolver()

    assert(CodeChecker.hasNoWarning)
    assert(ComponentManager.numberOfUnconnectedHardware() == 0)
    assert(ComponentManager.numberOfConnectedHardware() == 4)
    assert(r.getNumberOfPasses == 3)
    assert(res.size == 3)
    assert(res(0) === Set(c.btn1, c.cst1))
    assert(res(1) === Set(c.and1))
    assert(res(2) === Set(c.led1))
  }

  test("3 passes without warning in a different order") {
    ComponentManager.reset()
    val c = new ResolverCode4()
    val res = testResolver()

    assert(CodeChecker.hasNoWarning)
    assert(ComponentManager.numberOfUnconnectedHardware() == 0)
    assert(ComponentManager.numberOfConnectedHardware() == 4)
    assert(r.getNumberOfPasses == 3)
    assert(res.size == 3)
    assert(res(0) === Set(c.btn1, c.cst1))
    assert(res(1) === Set(c.and1))
    assert(res(2) === Set(c.led1))
  }

  test("5 passes with wait") {
    ComponentManager.reset()
    val c = new ResolverCode5()
    val res = testResolver()

    assert(CodeChecker.hasWarnings) // Some input are not connected
    assert(ComponentManager.numberOfUnconnectedHardware() == 0)
    assert(ComponentManager.numberOfConnectedHardware() == 6)
    assert(r.getNumberOfPasses == 5)
    assert(res.size == 5)
    assert(res(0) === Set(c.cst2, c.cst1))
    assert(res(1) === Set(c.and1))
    assert(res(2) === Set(c.and2))
    assert(res(3) === Set(c.and3))
    assert(res(4) === Set(c.led1))
  }
}