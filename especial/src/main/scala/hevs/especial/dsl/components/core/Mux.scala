package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

// FIXME: test version for 2 inputs and only boolean
case class Mux2(nbrInput: Int) extends Component with HwImplemented with In3 with Out1 {

  private type T = bool // FIXME

  /* Global variables names */
  private val valNameIn = for(i <- 0 to nbrInput) yield s"in${i+1}$getVarId" // in1Cmp5, in2Cmp5, etc
  private val valNameSel = s"sel$getVarId"
  private val valNameOut = s"out$getVarId"

  override val in1 = createInput(1)
  override val in2 = createInput(2)

  // Input n°3 is the selection pin
  val sel = new InputPort[T](this) {
    override val name = "sel"
    override val description = "the selection input"

    // Connection an output to the sel input
    override def setInputValue(s: String) = s"$valNameSel = $s"
  }

  override val in3 = sel

  private def createInput(n: Int) = new InputPort[T](this) {
    override val name = s"in$n"
    override def setInputValue(s: String) = s"${valNameIn(n-1)} = $s"
  }

  val out = new OutputPort[T](this) {
    override val name = s"out"
    override val description = "the selection input"

    override def getValue = {
      s"""
        |// ${Mux2.this}
        |${bool().getType} $valNameOut;
        | switch($valNameSel) {
        |  case 0:
        |    $valNameOut = ${valNameIn(0)};
        |    break;
        |
        |  case 1:
        |    $valNameOut = ${valNameIn(1)};
        |    break;
        |}
      """.stripMargin
    }
  }

  def getOutputs = Some(Seq(out))

  def getInputs = Some(Seq(in1, in2, sel))

  /* Code generation */

  // Global variables
  override def getGlobalCode = Some(s"${bool().getType} $valNameSel, ${valNameIn(0)}, ${valNameIn(1)}; // $this")

  override def getLoopableCode = out.isConnected match {
    case true =>
      val result: ListBuffer[String] = ListBuffer()

      // Mux code
      result += s"${out.getValue}"

      // Set the output value to connected components
      for (inPort ← ComponentManager.findConnections(out))
        result += inPort.setInputValue(s"$valNameOut") + "; // " + inPort

      Some(result.mkString("\n"))
    case _ => None
  }
}
