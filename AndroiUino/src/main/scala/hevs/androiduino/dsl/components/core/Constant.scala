package hevs.androiduino.dsl.components.core

import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.fundamentals._
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

case class Constant[T <: CType : TypeTag](value: T) extends Component with hw_implemented {

  override val description = "constant generator"

  private val valName = s"cstComp$getId" // unique name

  val out = new OutputPort[T](this) {

    override val description = "the constant value"

    override def getValue: String = valName
  }

  def getOutputs = Some(Seq(out))

  def getInputs = None

  /**
   * Constant declaration in the C code.
   * @return the constant declaration as boolean if the constant is connected
   */
  override def getGlobalConstants = out.isConnected match {
    case true =>
      // const bool_t cstComp1 = true;
      Some(s"const ${value.getType} $valName = ${value.v}; // $out")
    case false => None
  }

  override def getInitCode = out.isConnected match {
    case true =>
      val in = ComponentManager.findConnections(out)
      val results: ListBuffer[String] = ListBuffer()
      for (inPort ← in)
        results += inPort.setInputValue(valName) + "; // " + inPort
      Some(results.mkString("\n"))
    case false => None
  }
}