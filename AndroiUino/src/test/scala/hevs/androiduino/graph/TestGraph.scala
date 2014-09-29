package hevs.androiduino.graph

import hevs.androiduino.dsl.components.digital.DigitalInput

import scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._
import hevs.androiduino.dsl.components.fundamentals.hw_implemented
import hevs.androiduino.dsl.components.fundamentals.Component

object Test extends App {
  //var gr1 : Graph[Component, DiEdge] = Graph()
  val b1 = DigitalInput(3)
  val b2 = DigitalInput(4)
  //gr1 += b1 ~> b2
  var gr1 = Graph(b1 ~> b2)
  println(gr1)
}