package hevs.especial.genenator

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.digital.DigitalOutput
import hevs.especial.dsl.components.fundamentals.uint1
import hevs.especial.dsl.components.logic.And2

class Sch2Code {

  // Inputs
  val cst1 = Constant(uint1(true))
  val cst2 = Constant(uint1(true))

  // Logic
  val and1 = And2()

  // Output
  val led1 = DigitalOutput(7)

  // Connecting stuff
  and1.out --> led1.in
  cst1.out --> and1.in1
  cst1.out --> and1.in2

  // cst2.out --> and1(3) // Test with 14 // 1
}

class Sch2Test extends STM32TestSuite {

  def getDslCode = ""

  ComponentManager.reset()

  // The the main code
  new Sch2Code()

  // Generate the C code and the DOT graph
  //val code = CodeGenerator.generateCodeFile(fileName, fileName)
  //val dot = DotGenerator.generateDotFile(fileName, fileName)

  // Print code and dot as result
  //  println(code)
  //  println("\n***\n")
  //  println(dot)
}