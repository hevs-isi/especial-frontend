package hevs.androiduino.genenator

import hevs.androiduino.apps_old.TestGeneratorApp
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.logic.And
import hevs.androiduino.dsl.generator.{CodeGenerator, DotGenerator}

class Sch3Code {

  // Inputs
  val cst1 = Constant(uint1(true))
  val btn1 = DigitalInput(4)

  // Logic
  val and1 = And()

  // Output
  val led1 = DigitalOutput(7)

  // Connecting stuff
  and1.out --> led1.in
  cst1.out --> and1(1)
  btn1.out --> and1(2)
}

class Sch3Test extends TestGeneratorApp {

  ComponentManager.reset()

  // The the main code
  new Sch3Code()

  // Generate the C code and the DOT graph
  val code = CodeGenerator.generateCodeFile(fileName, fileName)
  val dot = DotGenerator.generateDotFile(fileName, fileName)

  // Print code and dot as result
//  println(code)
//  println("\n***\n")
//  println(dot)
}