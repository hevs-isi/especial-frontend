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
  // NC
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
    val c = new ResolverCode1()
    val warns = CodeGenerator.printWarnings()
    val hw = testResolver()

    assert(warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 0)
    assert(ComponentManager.findUnconnectedComponents.head == c.btn1)
    assert(r.getNumberOfPasses == 0) // Optimized, nothing to do
    assert(hw.isEmpty) // Nothing to resolve

    ComponentManager.unregisterComponents()
  }

  test("1 wire with 1 unconnected component") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode2()
    val warns = CodeGenerator.printWarnings()
    val hw = testResolver()

    assert(warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 2)
    assert(ComponentManager.findUnconnectedComponents.head == c.btn1)
    assert(r.getNumberOfPasses == 2)
    assert(hw.size == 2)
    hw should contain inOrderOnly(c.cst1, c.led1)

    ComponentManager.unregisterComponents()
  }

  test("3 passes without warning") {
    val c = new ResolverCode3()
    val warns = CodeGenerator.printWarnings()
    val hw = testResolver()

    assert(!warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 0)
    assert(ComponentManager.numberOfConnectedHardware() == 4)
    assert(r.getNumberOfPasses == 3)
    assert(hw.size == 4)
    hw should contain inOrderOnly(c.cst1, c.btn1, c.and1, c.led1)

    ComponentManager.unregisterComponents()
  }

  test("3 passes without warning, different order") {
    val c = new ResolverCode4()
    val hw = testResolver()

    hw should contain inOrderOnly(c.cst1, c.btn1, c.and1, c.led1)

    ComponentManager.unregisterComponents()
  }

  test("long resolver") {
    val c = new ResolverCode5()
    val hw = testResolver()

    println("HW: " + hw.mkString(", "))
    hw should contain inOrderOnly(c.cst1, c.and1, c.and2, c.cst2, c.and3, c.led1)

    ComponentManager.unregisterComponents()
  }
}