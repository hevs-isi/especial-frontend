package hevs.androiduino.genenator

import hevs.androiduino.apps.TestGeneratorApp
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.DigitalOutput
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.logic.And
import hevs.androiduino.dsl.generator.{CodeGenerator, DotGenerator}

class Sch4Code {

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

class Sch4Test extends TestGeneratorApp {

  // The the main code
  new Sch4Code()

  CodeGenerator.printWarnings()

  // Generate the C code and the DOT graph
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  val dot = DotGenerator.generateDotFile(ComponentManager.cpGraph, fileName, fileName)


  // Print code and dot as result
//  println(code)
//  println("\n***\n")
//  println(dot)
}