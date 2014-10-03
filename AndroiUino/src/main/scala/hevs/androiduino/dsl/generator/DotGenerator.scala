package hevs.androiduino.dsl.generator

import java.io.File

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.ComponentManager.Wire
import hevs.androiduino.dsl.components.fundamentals.Component
import hevs.androiduino.dsl.utils.OSUtils
import hevs.androiduino.dsl.utils.OSUtils.Linux

import scala.language.existentials
import scala.sys.process._
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge
import scalax.collection.io.dot._

object DotGenerator extends Logging {

  type T = Graph[Component, LDiEdge]

  def generateDotFile(graph: T, graphName: String, fileName: String): String = {
    val path = s"output/dot/$fileName.dot"
    val dot = generateDot(graph, graphName)
    val f: RichFile = new File(path) // Create the DOT file in the folder "output/dot/"
    f.write(dot)
    info(s"Dot file generated to '$path'.")
    convertDotToPdf(fileName) // Export the DOT image
    dot
  }

  def generateDot(graph: T, graphName: String): String = {
    new DotGenerator(graphName).generateDot(graph)
  }

  private def convertDotToPdf(fileName: String) = {
    val path = s"output/dot/$fileName"
    OSUtils.getOsType match {
      case _: Linux => s"dot $path.dot -Tpdf -o $path.pdf".!!
      case _ => sys.error("OS not supported. Cannot run `dot`.")
    }
    info(s"Dot PDF file generated to '$path.pdf'.")
  }
}

/**
 * Display all components of a graph to a DOT diagram.
 * The components are the nodes, and they are connected together with ports. The label on the edge describe to
 * connection from the OutputPort to the InputPort of two components.
 * @param graphName the name of the DOT graph to generate
 */
class DotGenerator(val graphName: String) {

  private val name = "\"" + graphName + "\""

  // Align nodes from the left to the right. Use orthogonal splines and add the title of the graph as a label.
  private val dotParams = Seq(DotAttr("rankdir", "LR"), DotAttr("splines", "line"), DotAttr("label", name))

  private val dotRoot = DotRootGraph(directed = true, id = Some("DotGraph"), kvList = dotParams)

  /**
   * Generate the DOT diagram of a graph.
   * @param g the graph to display
   * @return the dot file as a String
   */
  def generateDot(g: Graph[Component, LDiEdge]): String = {
    // Return the dot diagram as text
    g.toDot(dotRoot, edgeTransformer, cNodeTransformer = Option(cNodeTransformer))
  }

  /**
   * Transform all connected nodes.
   * @param innerNode graph nodes
   * @return the same transformation for all connected nodes of the graph
   */
  private def cNodeTransformer(innerNode: Graph[Component, LDiEdge]#NodeT):
  Option[(DotGraph, DotNodeStmt)] = {
    val node = innerNode.value.asInstanceOf[Component]

    Some(dotRoot, DotNodeStmt(nodeName(node), Seq(DotAttr("shape", "component"))))
  }

  /**
   * Transform all edges of the graph.
   * @param innerEdge graph edges
   * @return the same transformation for all edges of the graph
   */
  private def edgeTransformer(innerEdge: Graph[Component, LDiEdge]#EdgeT):
  Option[(DotGraph, DotEdgeStmt)] = {
    val edge = innerEdge.edge
    val label = edge.label.asInstanceOf[Wire]

    val nodeFrom = nodeName(edge.from.value.asInstanceOf[Component])
    val nodeTo = nodeName(edge.to.value.asInstanceOf[Component])
    Some(dotRoot,
      DotEdgeStmt(nodeFrom, nodeTo,
        List(DotAttr("label", labelName(label)))))
  }

  /**
   * Format the name of a Component to display it in a node.
   * @param c Component to display in a node
   * @return the node value
   */
  private def nodeName(c: Component): String = {
    // Display the component id and description on two lines
    val title = s"Cmp[${c.getId}]"
    val desc = c.getDescription
    title + "\\n" + desc
  }

  /**
   * Format a Wire to display it as a edge node.
   * @param w the wire to display as a edge label
   * @return the label value
   */
  private def labelName(w: Wire): String = {
    val portFrom = w.from // OutputPort
    val portTo = w.to // InputPort
    s"${portFrom.getDescription}-->${portTo.getDescription}"
  }
}