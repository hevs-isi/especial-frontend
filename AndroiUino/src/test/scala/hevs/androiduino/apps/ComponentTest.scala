package hevs.androiduino.apps

import java.io.PrintWriter

import hevs.androiduino.dsl.CodeGenerator
import hevs.androiduino.dsl.components.HW_Button
import hevs.androiduino.dsl.components.HW_Led

object ComponentTest extends App {
  //  val l1: SW_Led = new SW_Led()
  //	val c: Constant = new Constant(uint1())

  val l1 = HW_Led(4)
  val l2 = HW_Led(2)
  val l3 = HW_Led(6)

  val b1 = HW_Button(3)
  val b2 = HW_Button(5)

  //val c1 = Constant() 

  // Connecting stuff
  b1.out --> l2.in
  b1.out --> l1.in
  b2.out --> l3.in

  //	println(l2.getFullDescriptor)
  //	println(c.getFullDescriptor)
  //	println("Components present are \n\t" + ComponentManager.comps.mkString("\n\t"))

  //ComponentManager.generateInitCode();
  //ComponentManager.generateConstantsCode();
  //println(ComponentManager.generateLoopingCode());

  println("\n\n")

  val writer = new PrintWriter("output.c")
  writer.print(CodeGenerator.generateCode)
  writer.close()

  //l1.status = uint1(false)
}