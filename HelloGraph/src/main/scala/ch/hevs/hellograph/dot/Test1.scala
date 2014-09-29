import scalax.collection.Graph // or scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._

/***
 * Demonstrates the usage of objects in the graph
 * instead of existing types
 */
class MyObject(val content: String) {
  override def toString = s"n($content)"
}

object Test1 extends App {

   val obj1 = new MyObject("object1")
   val obj2 = new MyObject("object2")
   val obj3 = new MyObject("object3")

   val g1 = Graph(obj1 ~> obj2, obj2 ~> obj3, obj1 ~> obj3)

   // Look-up helper for known node (extract node from its name)
   def n(outer: MyObject) = g1 get outer

   println("g1: " + g1)

   // Find all direct successors
   
   println(n(obj1).diSuccessors.mkString(","))
   println(n(obj2).diSuccessors.mkString(","))
   println(n(obj3).diSuccessors.mkString(","))
}