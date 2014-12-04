package hevs.especial.dsl.components.core.logic

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

// FIXME: clean and check. Only for one input ?
// TODO: test case Sch7
case class Not[T <: CType : TypeTag]() extends Component with In1 with Out1 with HwImplemented {

  override val description = s"NOT gate"

  private val valName: String = outValName()

  /* I/O management */

  val in = new InputPort[T](this) {
    override val name = s"in"
    override val description = "input to invert"

    override def setInputValue(s: String) = {
      // Invert the value
      val sInvert = getTypeClass[T] match {
        case _: `Class`[bool] => s"!$s"
        case _ => s"""($valName == 0) ? 1 : 0"""
      }
      s"$valName = $sInvert"
    }
  }

  val out = new OutputPort[T](this) {
    override val name = s"out"
    override val description = "inverted value"

    override def getValue: String = valName // input already inverted
  }

  override def getOutputs= Some(Seq(out))

  override def getInputs = None


  /* Code generation */

  override def getGlobalCode = {
    Some(s"${bool().getType} $valName; // $out")
  }

  override def getLoopableCode = {
    // Propagate the output value to connected inputs
    val in = ComponentManager.findConnections(out)
    val results: ListBuffer[String] = ListBuffer()
    for (inPort <- in)
      results += inPort.setInputValue(out.getValue) + "; // " + inPort
    Some(results.mkString("\n"))
  }

}
