package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

case class Constant[T <: CType : TypeTag](value: T) extends Component with Out1 with HwImplemented {

  override val description = s"constant generator\\n(${value.v})"

  private val valName: String = valName("cst") // unique variable name

  /* I/O management */

  val out = new OutputPort[T](this) {
    override val name = s"out"
    override val description = "the constant value"
    override def getValue: String = valName
  }

  override def getOutputs= Some(Seq(out))

  override def getInputs = None

  /* Code generation */

  override def getGlobalCode = Some(s"const ${value.getType} $valName = ${value.v}; // $out")

  override def getInitCode = {
    val in = ComponentManager.findConnections(out)
    val results: ListBuffer[String] = ListBuffer()
    for (inPort ← in)
      results += inPort.setInputValue(valName) + "; // " + inPort
    Some(results.mkString("\n"))
  }
}