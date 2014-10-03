package hevs.androiduino.graph

import hevs.androiduino.dsl.components.digital.DigitalInput

import scalax.collection.GraphPredef._
import scalax.collection.mutable.Graph

object TestGraph extends App {
  //var gr1 : Graph[Component, DiEdge] = Graph()
  val b1 = DigitalInput(3)
  val b2 = DigitalInput(4)
  //gr1 += b1 ~> b2
  var gr1 = Graph(b1 ~> b2)
  println(gr1)
}