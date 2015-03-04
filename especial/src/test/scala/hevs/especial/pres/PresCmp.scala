package hevs.especial.pres

import hevs.especial.dsl.components.core.CFct
import hevs.especial.dsl.components._

/**
 * Math block used to adapt the speed of the fan.
 * @param gain speed gain from measure
 */
case class SpeedGain(gain: Int) extends CFct[uint32, int32]() {

  override val description = "Custom gain"

  /* I/O management */
  private val outVal = outValName()

  override def getOutputValue = outVal

  /* Code generation */
  override def loopCode = {
    val outType = getTypeString[int32]
    val in = getInputValue
    s"""|$outType $outVal = 4096 - (($in - 110) * $gain); // Speed gain
        |if ($outVal <= 0)
        |  $outVal = 0;""".stripMargin
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