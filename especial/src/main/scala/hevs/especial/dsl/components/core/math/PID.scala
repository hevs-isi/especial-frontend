package hevs.especial.dsl.components.core.math

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.Constant

/**
 * PID regulator.
 */
case class PID() extends Component with Out1 with HwImplemented {

  override val description = s"PID regulator"

  /* I/O management */

  val out = new OutputPort[double](this) {
    override val name = s"out"
    override val description = "the constant value"

    override def getValue: String = ???
  }

  override def getOutputs = Some(Seq(out))


  val kp = new InputPort[double](this) {
    override val name = "kp"
    override val description = "kp constant"

    override def setInputValue(s: String) = ???
  }

  val ki = new InputPort[double](this) {
    override val name = "ki"
    override val description = "ki constant"

    override def setInputValue(s: String) = ???
  }

  val kd = new InputPort[double](this) {
    override val name = "kd"
    override val description = "kd constant"

    override def setInputValue(s: String) = ???
  }

  val min = new InputPort[double](this) {
    override val name = "min"
    override val description = "min constant"

    override def setInputValue(s: String) = ???
  }

  val max = new InputPort[double](this) {
    override val name = "max"
    override val description = "max constant"

    override def setInputValue(s: String) = ???
  }

  val measure = new InputPort[double](this) {
    override val name = "measure"
    override val description = "input measure"

    override def setInputValue(s: String) = ???
  }

  val target = new InputPort[double](this) {
    override val name = "target"
    override val description = "target"

    override def setInputValue(s: String) = ???
  }

  override def getInputs = Some(Seq(kp, ki, kd, min, max, measure, target))
}

object PID {

  def apply(kp: Double, ki: Double, kd: Double, min: Double = 0.0, max: Double = 4096.0) = {
    import hevs.especial.dsl.components.ImplicitTypes._

    // Create and connect constant as PID settings
    val pid = new PID()
    Constant[double](kp).out --> pid.kp
    Constant[double](ki).out --> pid.ki
    Constant[double](kd).out --> pid.kd
    Constant[double](min).out --> pid.min
    Constant[double](max).out --> pid.max
    pid
  }
}
