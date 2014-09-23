package ch.hevs.hellograph

import scalax.collection.Graph
import scalax.collection.GraphPredef._ // Graph DSL - must be imported

object HelloGraph {
  def main(args: Array[String]) = {
    println("Hello Graph !")
    
    var gr1 = Graph("toto" ~> "titi")
    println(gr1)
  }
}
