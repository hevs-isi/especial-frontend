package hevs.especial.dsl.components.digital

import hevs.especial.dsl.components.In1
import hevs.especial.dsl.components.fundamentals.{InputPort, hw_implemented}

case class DigitalOutput(override val pin: Int) extends DigitalIO(pin) with In1 with hw_implemented {

  override val description = s"digital output on pin $pin"

  /**
   * The `uint1` value to write to this digital output.
   */
  override val in = new InputPort[T](this) {

    override val description = "digital output value"

    override def setInputValue(s: String): String = s"DigitalOutput($pin).set($s)"
  }

  def getOutputs = None

  def getInputs = Some(Seq(in))

  override def getInitCode = in.isConnected match {
    case true => Some(s"DigitalOutput($pin).initialize(); // Init of $this")
    case _ => None
  }

  override def getIncludeCode = Some( """|#include "digitaloutput.h"""".stripMargin)
}