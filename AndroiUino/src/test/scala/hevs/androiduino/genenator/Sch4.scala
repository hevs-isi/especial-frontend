package hevs.androiduino.genenator

import hevs.androiduino.apps_old.TestGeneratorApp
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.DigitalOutput
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.logic.And
import hevs.androiduino.dsl.generator.{CodeGenerator, DotGenerator}

class Sch4Code {

  val cst1 = Constant(uint1(false))
  val cst2 = Constant(uint1(false))
  val and1 = And()
  val and2 = And()
  val and3 = And(4)
  val led1 = DigitalOutput(7)

  cst2.out --> and3(2)
  cst1.out --> and1(1)
  and1.out --> and2(2)
  and2.out --> and3(4)
  and3.out --> led1.in
}

class Sch4Test extends TestGeneratorApp {

  ComponentManager.reset()

  // The the main code
  new Sch4Code()

  // Generate the C code and the DOT graph
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  val dot = DotGenerator.generateDotFile(fileName, fileName)

  // Print code and dot as result
  //  println(code)
  //  println("\n***\n")
  //  println(dot)
}