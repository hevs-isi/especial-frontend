package hevs.androiduino.dsl.components.logic

import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.fundamentals._

import scala.collection.mutable.ListBuffer

// TODO pattern for generic logical gates (uint1) with operator, 2,3..xx inputs

// And2: 2 in, 1 out
case class And() extends Component with hw_implemented {

  override val description = "and2 gate"
  val out = new OutputPort[uint1](this) {
    override val description = "the AND output"

    override def getValue: String = s"$in1 & $in2"
  }
  val in1 = new InputPort[uint1](this) {
    override val description = "input 1"

    // Use global variable
    override def setInputValue(s: String): String = s"$valName1 = $s"
  }
  val in2 = new InputPort[uint1](this) {
    override val description = "input 2"

    // Use global variable
    override def setInputValue(s: String): String = s"$valName2 = $s"
  }
  private val tpe = uint1().getType
  private val valName1 = s"in1Comp$getId"
  private val valName2 = s"in2Comp$getId"
  private val valName3 = s"outComp$getId"

  def getOutputs = Some(Seq(out))

  def getInputs = Some(Seq(in1, in2))

  override def getGlobalCode = out.isConnected match {
    // Input variables declarations of the gate
    case true => Some(s"$tpe $valName1, $valName2; // $this")
    case _ => None
  }

  override def getLoopableCode = out.isConnected match {
    case true =>
      val result: ListBuffer[String] = ListBuffer()

      // Compute the result from the global variables
      result += s"$tpe $valName3 = ($valName1 & $valName2);"

      // Read inputs of the and gate
      val in = ComponentManager.findConnections(out)
      for (inPort ← in)
        result += inPort.setInputValue(s"$valName3") + "; // " + inPort

      Some(result.mkString("\n"))
    case _ => None
  }

}
