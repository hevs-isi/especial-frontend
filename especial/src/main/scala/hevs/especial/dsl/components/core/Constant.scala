package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

case class Constant[T <: CType : TypeTag](value: T) extends Component with Out1 with hw_implemented {

  override val description = "constant generator"
  private val valName = s"cst$getVarId" // unique variable name

  val out = new OutputPort[T](this) {
    override val description = "the constant value"
    override def getValue: String = valName
  }

  def getOutputs = Some(Seq(out))

  def getInputs = None

  override def getGlobalCode = Some(s"const ${value.getType} $valName = ${value.v}; // $out")

  override def getInitCode = {
    val in = ComponentManager.findConnections(out)
    val results: ListBuffer[String] = ListBuffer()
    for (inPort ← in)
      results += inPort.setInputValue(valName) + "; // " + inPort
    Some(results.mkString("\n"))
  }
}