package hevs.androiduino.dsl.generator

import hevs.androiduino.dsl.components.ComponentManager.Wire
import hevs.androiduino.dsl.components.fundamentals.Component

import scalax.collection.Graph
import scalax.collection.edge.LDiEdge
import scalax.collection.io.dot._

class DotGenerator {

  lazy val dotParams = Seq(DotAttr("rankdir", "LR"), DotAttr("splines", "ortho"))

  lazy val dotRoot = DotRootGraph(directed = true,
    id = Some("GraphExport"), kvList = dotParams)

  def generate(g: Graph[Component, LDiEdge]) = {
    val dot = g.toDot(dotRoot, edgeTransformer)
    println(dot)
  }

  /*private def nodeTransformer(innerNode: Graph[Component, LDiEdge]#NodeT):
  Option[(DotGraph, DotNodeStmt)] =
    Some(dotRoot, DotNodeStmt(innerNode.toString(), Seq.empty[DotAttr])) //Seq(DotAttr("shape", "component"))))*/

  private def edgeTransformer(innerEdge: Graph[Component, LDiEdge]#EdgeT):
  Option[(DotGraph, DotEdgeStmt)] = {
    val edge = innerEdge.edge
    val label = edge.label.asInstanceOf[Wire]

    val nodeFrom = getNodeName(edge.from.value.asInstanceOf[Component])
    val nodeTo = getNodeName(edge.to.value.asInstanceOf[Component])

    /*Some(dotRoot,
      DotEdgeStmt(edge.from.toString(),
        edge.to.toString(),
        List(DotAttr("label", label.to.toString))))*/
    Some(dotRoot,
      DotEdgeStmt(nodeFrom, nodeTo,
        List(DotAttr("label", getLabelName(label)), DotAttr("shape", "component"))))
  }

  private def getNodeName(c: Component) = {
    // Display the component id and description on two lines
    val title = s"Cmp[${c.getId}]"
    val desc = c.getDescription
    title + "\\n" + desc
  }

  private def getLabelName(w: Wire) = {
    val portFrom = w.from // OutputPort
    val portTo = w.to // InputPort
    s"${portFrom.getDescription}-->${portTo.getDescription}"
  }
}