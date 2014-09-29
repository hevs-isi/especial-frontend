import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.io.dot._
import scalax.collection.edge.LDiEdge
import java.io.File
import ch.hevs.hellograph.dot.RichFile
import scala.language.existentials

class ObjToExport(val content: String) {
  override def toString() = content
}

object DotExport extends App {

  val obj1 = new ObjToExport("Obj1")
  val obj2 = new ObjToExport("Obj2")
  val obj3 = new ObjToExport("Obj3")

  val g1 = Graph(obj1 ~> obj2, obj2 ~> obj3, obj1 ~> obj3)

  val root = DotRootGraph(directed = true, id = Some("\"Dot export example\""))

  def edgeTransformer(innerEdge: Graph[ObjToExport, DiEdge]#EdgeT): Option[(DotGraph, DotEdgeStmt)] = {
    val edge = innerEdge.edge

    Some(root,
      DotEdgeStmt(edge.from.toString,
        edge.to.toString,
        Nil))
  }

  val dot = g1.toDot(root, edgeTransformer)

  // Put into file
  val f: RichFile = new File("graph/dotExport.txt")
  f write dot.toString
  println(dot.toString)
}