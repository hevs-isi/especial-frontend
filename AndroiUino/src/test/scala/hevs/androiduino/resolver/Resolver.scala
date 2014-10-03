package hevs.androiduino.resolver

import hevs.androiduino.apps.TestGeneratorApp
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.logic.And
import hevs.androiduino.dsl.generator.{CodeGenerator, Resolver}

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

class ResolverTest extends TestGeneratorApp {

  test("1 unconnected component") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode1()
    val r = new Resolver()
    val warns = CodeGenerator.printWarnings()
    r.resolveGraph()

    assert(warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 0)
    assert(ComponentManager.findUnconnectedComponents.head == c.btn1)
    assert(r.getNumberOfPasses == 0)
    assert(r.getNumberOfCodes == 0)
  }

  test("1 wire with 1 unconnected component") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode2()
    val r = new Resolver()
    val warns = CodeGenerator.printWarnings()
    r.resolveGraph()

    assert(warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 2)
    assert(ComponentManager.findUnconnectedComponents.head == c.btn1)
    assert(r.getNumberOfPasses == 2)
    assert(r.getNumberOfCodes == 2)
  }

  test("3 passes without warning") {
    ComponentManager.unregisterComponents()
    new ResolverCode3()
    val r = new Resolver()
    val warns = CodeGenerator.printWarnings()
    r.resolveGraph()

    assert(!warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 0)
    assert(ComponentManager.numberOfConnectedHardware() == 4)
    assert(r.getNumberOfPasses == 3)
    assert(r.getNumberOfCodes == 4)
  }
}