import scalax.collection.Graph // or scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._

// A way to define equality of objects when nodes are defined with attributes
class AnObject(val content: String) {

   // We need to define equality for our objects
   override def equals(other: Any) = {
      other match {
         case that: MyObject ⇒ that.content == this.content
         case _ ⇒ false
      }
   }

   // Hashcode also required for working with graphs
   // The meaning of ## is cryptic here but related to hashcode equality
   // for autoboxed type in Java 
   override def hashCode() = content.##   

   override def toString() = content
}

object Test2 extends App {

   val obj1 = new AnObject("object1")
   val obj2 = new AnObject("object2")
   val obj3 = new AnObject("object3")

   val g1 = Graph(obj1 ~> obj2, obj2 ~> obj3, obj1 ~> obj3)

   // Look-up helper for known node (extract node from its name)
   def n(outer: AnObject) = g1 get outer

   println(g1)

   // Find all successors of A
   println(n(obj1).diSuccessors.mkString(","))
   // Find all successors of B
   println(n(obj2).diSuccessors.mkString(","))
   // Find all successors of C
   println(n(obj3).diSuccessors.mkString(","))
   println(g1.findCycle)
}