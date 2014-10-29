package hevs.especial.dsl.components.digital

import hevs.especial.dsl.components.ComponentManager
import hevs.especial.dsl.components.fundamentals.{OutputPort, hw_implemented, uint1}

import scala.collection.mutable.ListBuffer

case class DigitalInput(override val pin: Int) extends DigitalIO(pin) with hw_implemented {

  override val description = s"digital input on pin $pin"

  private val fctName = s"pollDigitalInput$pin"

  /**
   * The `uint1` value of this digital input.
   */
  val out = new OutputPort[T](this) {

    override val description = "digital input value"

    override def getValue: String = s"pollButton$pin();"
  }

  def getOutputs = Some(Seq(out))

  def getInputs = None

  override def getInitCode = out.isConnected match {
    case true => Some(s"DigitalInput($pin).initialize(); // Init of $this")
    case _ => None
  }

  override def getLoopableCode = out.isConnected match {
    case true => Some(s"$fctName();")
    case _ => None
  }

  override def getFunctionsDefinitions = out.isConnected match {
    case true =>
      val result: ListBuffer[String] = ListBuffer()
      result += s"void $fctName() {"
      result += "if(valueHasChanged) {"
      result += s"${uint1().getType} val = DigitalInput($pin).read();"

      val in = ComponentManager.findConnections(out)
      for (inPort ← in)
        result += inPort.setInputValue("val") + "; // " + inPort

      result += "}"
      result += "}"
      Some(result.mkString("\n"))
    case _ => None
  }
}