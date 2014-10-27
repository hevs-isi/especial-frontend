package hevs.especial.genenator

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.digital.{DigitalInput, DigitalOutput}
import hevs.especial.dsl.components.fundamentals.uint1
import hevs.especial.dsl.components.logic.And2

class Sch3Code {

  // Inputs
  val cst1 = Constant(uint1(true))
  val btn1 = DigitalInput(4)

  // Logic
  val and1 = And2()

  // Output
  val led1 = DigitalOutput(7)

  // Connecting stuff
  and1.out --> led1.in
  cst1.out --> and1.in1
  btn1.out --> and1.in2
}

class Sch3Test extends STM32TestSuite {

  def getDslCode = ""

  ComponentManager.reset()

  // The the main code
  new Sch3Code()

  // Generate the C code and the DOT graph
  // val code = CodeGenerator.generateCodeFile(fileName, fileName)
  // val dot = DotGenerator.generateDotFile(fileName, fileName)

  // Print code and dot as result
//  println(code)
//  println("\n***\n")
//  println(dot)
}