package hevs.androiduino.graph

import scalax.collection.mutable.Graph
import hevs.androiduino.dsl.components.HW_Button
import scalax.collection.GraphPredef._
import hevs.androiduino.dsl.components.fundamentals.hw_implemented
import hevs.androiduino.dsl.components.fundamentals.Component

case class Toto() extends Component("pouet") with hw_implemented {
  def getInputs(): List[hevs.androiduino.dsl.components.fundamentals.InputPort] = { Nil }
  def getOutputs(): List[hevs.androiduino.dsl.components.fundamentals.OutputPort] = { Nil }
}

object Test extends App {
  //var gr1 : Graph[Component, DiEdge] = Graph()
  val b1 = HW_Button(3)
  val b2 = HW_Button(4)
  //gr1 += b1 ~> b2
  var gr1 = Graph(b1 ~> b2)
  println(gr1)
}