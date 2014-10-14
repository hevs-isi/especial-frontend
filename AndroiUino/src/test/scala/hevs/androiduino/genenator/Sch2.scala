package hevs.androiduino.genenator

import hevs.androiduino.apps_old.TestGeneratorApp
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.DigitalOutput
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.logic.And
import hevs.androiduino.dsl.generator.{CodeGenerator, DotGenerator}

class Sch2Code {

  // Inputs
  val cst1 = Constant(uint1(true))
  val cst2 = Constant(uint1(true))

  // Logic
  val and1 = And(3)

  // Output
  val led1 = DigitalOutput(7)

  // Connecting stuff
  and1.out --> led1.in
  cst1.out --> and1(2)
  cst1.out --> and1(3)

  // cst2.out --> and1(3) // Test with 14 // 1
}

class Sch2Test extends TestGeneratorApp {

  ComponentManager.reset()

  // The the main code
  new Sch2Code()

  // Generate the C code and the DOT graph
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  val dot = DotGenerator.generateDotFile(ComponentManager.cpGraph, fileName, fileName)

  // Print code and dot as result
  //  println(code)
  //  println("\n***\n")
  //  println(dot)
}