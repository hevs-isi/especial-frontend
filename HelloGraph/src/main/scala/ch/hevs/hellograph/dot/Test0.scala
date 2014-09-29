import scalax.collection.Graph // or scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._

object Test0 extends App {

   val edge1 = "A" ~> "B"
   val edge2 = "B" ~> "C"
   val edge3 = "A" ~> "C"

   val g1 = Graph(edge1, edge2, edge3)

   // Look-up helper for known node (extract node from its name)
   def n(outer: String) = g1 get outer

   // Print nodes and edges
   println("The whole graph " + g1)
   println("Edges only " + g1.edges.mkString(","))
   println("Nodes only " + g1.nodes.mkString(","))
   
   // Find all successors of A
   println("Successors of node A are " + n("A").diSuccessors.mkString(","))
   // Find all successors of B
   println("Successors of node B are " + n("B").diSuccessors.mkString(","))
   // Find all successors of C
   println("Successors of node C are " + n("C").diSuccessors.mkString(","))
   
   // Cycle detector
   println(g1.findCycle)
   
   // Add a cycle in the graph
   val g2 = g1 + "C" ~> "A"
   println(g2.findCycle)
}