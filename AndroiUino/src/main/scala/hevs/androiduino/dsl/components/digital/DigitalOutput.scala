package hevs.androiduino.dsl.components.digital

import hevs.androiduino.dsl.components.fundamentals.{InputPort, hw_implemented, uint1}

case class DigitalOutput(override val pin: Int) extends DigitalIO(pin) with hw_implemented {

  override val description = s"Digital output on pin $pin"

  /**
   * The `uint1` value to write to this digital output.
   */
  val in = new InputPort[T](this) {

    override val description = "Digital output value"

    override def updateValue(s: String): String = {
      // TODO: Here is the code for setting the LED !
      s"led$pin = $s"
    }
  }

  def getOutputs = None

  def getInputs = Some(Seq(in))

  override def getInitCode = {
    Some(
      s"""
        |// Led component initialization
        |int reg_Comp$id = init_value; // TODO replace this with the real code
      """.stripMargin
    )
  }
}