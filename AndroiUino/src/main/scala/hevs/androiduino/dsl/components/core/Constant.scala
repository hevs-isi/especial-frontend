package hevs.androiduino.dsl.components.core

import hevs.androiduino.dsl.components.fundamentals._

import scala.reflect.runtime.universe._

case class Constant[T <: CType : TypeTag](value: T) extends Component with hw_implemented {

  override val description = "Constant generator"

  val valName = s"cstComp${id}" // unique name

  val out = new OutputPort[T](this) {

    override val description = "The constant value"

    override def getValue: String = valName
  }

  def getOutputs = Some(Seq(out))

  def getInputs = None

  /**
   * Constant declaration in the C code.
   * @return the constant declaration as boolean if the constant is connected
   */
  override def getGlobalConstants = out.isConnected match {
    case true => {
      // const bool_t cstComp1 = true;
      Some(s"const ${value.getType} $valName = ${value.v};")
    }
    case false => None
  }

  override def getBeginOfMainAfterInit = out.isConnected match {
    case true => {

      val in = ComponentManager.findConnection(out.getOwnerId)

      val result = "// TODO: Propagating constants\n\n"

      /*for (wire ← out.wires)
        result += wire.to.readValue(s"$valName") + ";\n"*/

      Some(result + "// " + in + "\n")
    }
    case false => None
  }
}