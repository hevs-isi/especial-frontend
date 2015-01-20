package hevs.especial.dsl.components.core.math

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.Constant
import hevs.especial.utils.Settings

/**
 * PID regulator.
 *
 * Constant can be used to set Kp, Ki and Kd values of the regulator. They can be updated when the code is running.
 * All input values are [[double]] values. The result of the regulator is an [[uint16]] value. This output can have a
 * limit range. Minimum and maximum values can be also set using [[double]] values.
 *
 * A companion object is available to connect the regulator with ease.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
case class PID() extends Component with Out1 with HwImplemented {

  override val description = s"PID regulator"

  /* I/O management */

  val out = new OutputPort[uint16](this) {
    override val name = s"out"
    override val description = "output value"

    override def getValue: String = s"$outName"
  }

  override def getOutputs = Some(Seq(out))


  val kp = new InputPort[double](this) {
    override val name = "kp"
    override val description = "kp constant"
  }

  val ki = new InputPort[double](this) {
    override val name = "ki"
    override val description = "ki constant"
  }

  val kd = new InputPort[double](this) {
    override val name = "kd"
    override val description = "kd constant"
  }

  val min = new InputPort[double](this) {
    override val name = "min"
    override val description = "min constant"
  }

  val max = new InputPort[double](this) {
    override val name = "max"
    override val description = "max constant"
  }

  val measure = new InputPort[int32](this) {
    override val name = "measure"
    override val description = "input measure"
  }

  val setpoint = new InputPort[uint16](this) {
    override val name = "setpoint"
    override val description = "setpoint"
  }

  override def getInputs = Some(Seq(kp, ki, kd, min, max, measure, setpoint))

  /* Code generation */

  private val valName: String = valName("pid")
  private val outName: String = outValName()

  override def getGlobalCode = {
    val res = s"pid_t $valName;"
    if (Settings.GEN_VERBOSE_CODE)
      Some(res + s"\t\t\t\t\t// $out") // Print a description of the output
    else
      Some(res)
  }

  override def getInitCode = {
    val vKp = ComponentManager.findPredecessorOutputPort(kp).getValue
    val vKi = ComponentManager.findPredecessorOutputPort(ki).getValue
    val vKd = ComponentManager.findPredecessorOutputPort(kd).getValue

    val vMin = ComponentManager.findPredecessorOutputPort(min).getValue
    val vMax = ComponentManager.findPredecessorOutputPort(max).getValue

    // PID initialisation
    Some(s"pid_init(&$valName, $vKp, $vKi, $vKd, $vMin, $vMax);")
  }

  override def getLoopableCode = {
    // Read the measure and the setpoint value
    val vMes = ComponentManager.findPredecessorOutputPort(measure).getValue
    val vPoint = ComponentManager.findPredecessorOutputPort(setpoint).getValue

    val res = new StringBuilder
    res ++= s"$valName.state.setpoint = $vPoint;\n"
    res ++= s"${uint16().getType} $outName = pid_step(&$valName, $vMes);"
    Some(res.result())
  }

  override def getIncludeCode = Seq("demo/pid.h")
}

object PID {

  import hevs.especial.dsl.components.CType.Implicits._

  def apply(kp: Double, ki: Double, kd: Double, min: Double = 0.0, max: Double = 4096.0) = {
    // Create and connect constant as PID settings
    val pid = new PID()
    Constant[double](kd).out --> pid.kd
    Constant[double](kp).out --> pid.kp
    Constant[double](ki).out --> pid.ki
    Constant[double](min).out --> pid.min
    Constant[double](max).out --> pid.max
    pid
  }
}
