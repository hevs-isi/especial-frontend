package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals.{Component, InputPort, hw_implemented, uint1}

abstract class Led(description: String) extends Component(description) {
  //	protected[this] var _status: uint1 = uint1(false)
}

case class HW_Led(pin: Int) extends Led("an hardware led on pin " + pin) with hw_implemented {

  // Anonymous mixin of the trait
  val in = new InputPort(uint1(), this) {
    override def updateValue(s: String): String = {
      // TODO: Here is the code for setting the LED !
      s"led$pin = $s"
    }
  }

  override def getOutputs = None

  override def getInputs = Some(Seq(in))

  override def getInitCode = {
    Some(
      s"""
        |// Led component initialization
        |int reg_Comp$id = init_value; // TODO replace this with the real code
      """.stripMargin
    )
  }
}