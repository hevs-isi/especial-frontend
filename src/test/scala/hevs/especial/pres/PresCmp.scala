package hevs.especial.pres

import hevs.especial.dsl.components.core.CFct
import hevs.especial.dsl.components._

/**
 * Math block used to adapt the speed of the fan.
 * @param gain speed gain from measure
 */
case class SpeedGain(gain: Double) extends CFct[uint32, int32]() {

  override val description = "Custom gain"

  override val globalVars: Map[String, double] = Map("rc" -> double(0))

  /* I/O management */
  private val outVal = outValName()

  override def getOutputValue = outVal

  /* Code generation */
  override def loopCode = {
    val outType = getTypeString[int32]
    val pid = ComponentManager.findPredecessorOutputPort(this.in)
    val in = pid.getValue

    s"""|${getTypeString[uint32]} timeDiff = time_diff_ms(time_get(), ${pid.getOwner.inValName()}.lastPulseTimestamp);
        |$outType $outVal = MAX($in , timeDiff);
        |rc = rc * 0.5 + $outVal * 0.5; // RC filter
        |$outVal = $gain / rc;       // Speed gain
        |
        |delay_wait_ms(10);""".stripMargin
  }
}

/** Custom C component to invert a [[bool]] value and return an [[uint8]] value. */
case class Not() extends CFct[bool, uint8]() {

  override val description = "Inverter to uint8"

  /* I/O management */
  private val outVal = outValName()

  override def getOutputValue = outVal

  /* Code generation */
  override def loopCode = {
    val outType = getTypeString[uint8]
    val in = getInputValue
    s"$outType $outVal = ($in == 0) ? 1 : 0;"
  }
}

/**
 * Custom threshold component.
 *
 * Logic implemented in C. The output is `false` when the input value is above the threshold value,
 * and `true` otherwise (this is a basic trigger and not a Schmitt trigger).
 *
 * @param threshold the threshold value (between 0x0 and 0xFFFF)
 */
case class Threshold(threshold: Int = 512) extends CFct[uint16, bool]() {

  override val description = s"value $threshold"

  /* I/O management */
  private val outVal = valName("threshold")

  override def getOutputValue: String = s"$outVal"

  /* Code generation */
  override def loopCode = {
    val outType = getTypeString[bool]
    val in = getInputValue
    s"""| $outType $outVal = false;
        | if($in > $threshold)
        |   $outVal = true;   """.stripMargin
  }
}