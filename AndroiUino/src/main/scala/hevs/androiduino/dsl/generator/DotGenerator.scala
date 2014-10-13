package hevs.androiduino.dsl.generator

import java.io.File

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.ComponentManager.Wire
import hevs.androiduino.dsl.components.fundamentals.{Component, InputPort, OutputPort, Port}
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
 * Components are the nodes, connected together with ports. The label on the edge describe the type
 * of the connection - from an OutputPort to an InputPort of two components. Unconnected ports are display as "NC".
 * Unconnected components (nodes) are in orange.
 * @param graphName the name of the DOT graph to generate
 */
class DotGenerator(val graphName: String) {

  private val name = "\"" + graphName + "\""

  // Align nodes from the left to the right. Use orthogonal splines and add the title of the graph as a label.
  private val dotParams = Seq(DotAttr("label", name), DotAttr("rankdir", "TB"))

  private val dotRoot = DotRootGraph(directed = true, id = Some("DotGraph"), kvList = dotParams)

  /**
   * Generate the DOT diagram of a graph.
   * @param g the graph to display
   * @return the dot file as a String
   */
  def generateDot(g: Graph[Component, LDiEdge]): String = {
    // Return the dot diagram as text
    g.toDot(dotRoot, edgeTransformer, iNodeTransformer = Option(nodeTrans), cNodeTransformer = Option(nodeTrans))
  }

  /**
   * Transform all connected nodes.
   * @param innerNode graph nodes
   * @return the same transformation for all connected nodes of the graph
   */
  private def nodeTrans(innerNode: Graph[Component, LDiEdge]#NodeT):
  Option[(DotGraph, DotNodeStmt)] = {
    val n = innerNode.value.asInstanceOf[Component]

    // The label is something like: {<in1>in1|<in2>in2}|Cmp[01]|{<out1>out1|<out2>out2}
    val in = makeLabelList(n.getInputs.getOrElse(Nil))
    val out = makeLabelList(n.getOutputs.getOrElse(Nil))
    val label = s"{$in}|${nodeName(n)}|{$out}"

    val color = if (n.isConnected) "black" else "orange"

    Some(dotRoot, DotNodeStmt(nodeId(n), Seq(DotAttr("label", label), DotAttr("shape", "Mrecord"), DotAttr("color",
      color))))
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
    val connected = if (c.isConnected) "" else " (NC)"
    s"$title$connected\\n$desc"
  }

  /**
   * Return the component ID as String.
   * @param c the Component
   * @return the ID as String
   */
  private def nodeId(c: Component): String = c.getId.toString

  /**
   * Format a list of input or output of a component. Check if it is connected or not and display it.
   * @param l list of input or output of the component
   * @return list formatted for dot record structure
   */
  private def makeLabelList(l: Seq[Port[_]]) = {
    // Return the ID of the port with a label
    l.map(
      x => {
        val id = x.getId
        val nc = if (x.isNotConnected) " (NC)" else ""
        x match {
          case _: InputPort[_] => s"<$id>In[$id]$nc"
          case _: OutputPort[_] => s"<$id>Out[$id]$nc"
        }
      }
    ).mkString("|")
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

    val nodeFrom = edge.from.value.asInstanceOf[Component].getId
    val nodeTo = edge.to.value.asInstanceOf[Component].getId
    Some(dotRoot,
      DotEdgeStmt(nodeFrom + ":" + label.from.getId, nodeTo + ":" + label.to.getId,
        List(DotAttr("label", labelName(label)))))
  }

  /**
   * Display the type of the connection on thw wire.
   * @param w the wire to display as a edge label
   * @return the type of the connection as a label value
   */
  private def labelName(w: Wire): String = {
    // Something like "hevs.androiduino.dsl.components.fundamentals.uint1"
    val t = w.from.getType

    // Return the child class (ex: uint1) as String
    t.baseClasses.head.asClass.name.toString
  }
}