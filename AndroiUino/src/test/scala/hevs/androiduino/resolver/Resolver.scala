package hevs.androiduino.resolver

import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.logic.And
import hevs.androiduino.dsl.generator.CodeGenerator

class ResolverCode1 {
  // Nothing to do, no inputs
  val btn1 = DigitalInput(4)
}

// 1 pass, 1 unconnected
class ResolverCode2 {
  val cst1 = Constant(uint1(false))
  val btn1 = DigitalInput(4)
  val led1 = DigitalOutput(7)
  cst1.out --> led1.in
}

// 3 passes without warning
class ResolverCode3 {
  val cst1 = Constant(uint1(false))
  val btn1 = DigitalInput(4)
  val led1 = DigitalOutput(7)
  val and1 = And()

  cst1.out --> and1.in1
  btn1.out --> and1.in2
  and1.out --> led1.in
}

// 3 passes without warning
class ResolverCode4 {
  // Same as ResolverCode3 in a different order
  val led1 = DigitalOutput(7)
  val cst1 = Constant(uint1(false))
  val and1 = And()
  val btn1 = DigitalInput(4)

  and1.out --> led1.in
  btn1.out --> and1.in2
  cst1.out --> and1.in1
}

class ResolverCode5 {
  val cst1 = Constant(uint1(false))
  val and1 = And()
  val and2 = And()
  val and3 = And()
  val cst2 = Constant(uint1(false))
  val led1 = DigitalOutput(7)

  cst1.out --> and1.in1
  and1.out --> and2.in2
  and2.out --> and3.in1
  cst2.out --> and3.in2
  and3.out --> led1.in
}

/**
 * Test specification for the `Resolver` class. Contains also some tests  for the `ComponentManager`.
 * The order of the component should be respected. Each component are identified by a unique ID.
 */
class ResolverTest extends ResolverTestSpec {

  test("1 unconnected component") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode1()
    val warns = CodeGenerator.printWarnings()
    val hw = testResolver()

    assert(warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 0)
    assert(ComponentManager.findUnconnectedComponents.head == c.btn1)
    assert(r.getNumberOfPasses == 0) // Optimized, nothing to do
    assert(hw.isEmpty) // Nothing to resolve
  }

  test("1 wire with 1 unconnected component") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode2()
    val warns = CodeGenerator.printWarnings()
    val res = testResolver()

    assert(warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 2)
    assert(ComponentManager.findUnconnectedComponents.head == c.btn1)
    assert(r.getNumberOfPasses == 2)
    assert(res.size == 2)
    assert(res(0) === Set(c.cst1))
    assert(res(1) === Set(c.led1))
  }

  test("3 passes without warning") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode3()
    val warns = CodeGenerator.printWarnings()
    val res = testResolver()

    assert(!warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 0)
    assert(ComponentManager.numberOfConnectedHardware() == 4)
    assert(r.getNumberOfPasses == 3)
    assert(res.size == 3)
    assert(res(0) === Set(c.btn1, c.cst1))
    assert(res(1) === Set(c.and1))
    assert(res(2) === Set(c.led1))
  }

  test("3 passes without warning, different order") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode4()
    val warns = CodeGenerator.printWarnings()
    val res = testResolver()

    assert(!warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 0)
    assert(ComponentManager.numberOfConnectedHardware() == 4)
    assert(r.getNumberOfPasses == 3)
    assert(res.size == 3)
    assert(res(0) === Set(c.btn1, c.cst1))
    assert(res(1) === Set(c.and1))
    assert(res(2) === Set(c.led1))
  }

  test("5 passes with wait") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode5()
    val warns = CodeGenerator.printWarnings()
    val res = testResolver()

    assert(!warns)
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