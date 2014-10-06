package hevs.androiduino.resolver

import hevs.androiduino.apps.TestGeneratorApp
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.androiduino.dsl.components.fundamentals.{hw_implemented, uint1}
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

// TODO: faire joli la classe abstraite pour le test du resolver ;)
// TODo: chercher un template sur Google pour la classe de test ?
class ResolverTest extends TestGeneratorApp {

  def toListIDs(cp: Seq[hw_implemented]): Unit = {

    // TODO: extract ID from components so it is easy to check the result of the resolver !

  }

  test("1 unconnected component") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode1()
    val r = new Resolver()
    val warns = CodeGenerator.printWarnings()
    val cp = r.resolveGraph()

    assert(warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 0)
    assert(ComponentManager.findUnconnectedComponents.head == c.btn1)
    assert(r.getNumberOfPasses == 0)  // Optimized, nothing to do
    assert(cp.isEmpty)  // None
  }

  test("1 wire with 1 unconnected component") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode2()
    val r = new Resolver()
    val warns = CodeGenerator.printWarnings()
    val cp = r.resolveGraph()

    assert(warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 1)
    assert(ComponentManager.numberOfConnectedHardware() == 2)
    assert(ComponentManager.findUnconnectedComponents.head == c.btn1)
    assert(r.getNumberOfPasses == 2)
    assert(cp.get.size == 2)
    println("Code2: " + cp.mkString(", "))
  }

  test("3 passes without warning") {
    ComponentManager.unregisterComponents()
    val c = new ResolverCode3()
    val r = new Resolver()
    val warns = CodeGenerator.printWarnings()
    val cp = r.resolveGraph()

    assert(!warns)
    assert(ComponentManager.numberOfUnconnectedHardware() == 0)
    assert(ComponentManager.numberOfConnectedHardware() == 4)
    assert(r.getNumberOfPasses == 3)
    assert(cp.get.size == 4)
    println("Code3: " + cp.mkString(", "))
  }
}