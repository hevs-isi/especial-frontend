package hevs.especial.doc

import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.core.logic.Not
import hevs.especial.dsl.components.core.math.{Mul2, Sub2}
import hevs.especial.dsl.components.uint32
import hevs.especial.generator.STM32TestSuite

/**
 * Speed gain block implementation using available math components.
 *
 * Same implementation as the [[hevs.especial.apps.FanPid.SpeedGain]] block.
 * These math blocks can replace the custom C component. Inputs and outputs must be connected to the pulse counter
 * and to the PID input measure.
 * This code only generates the DOT diagram used in the documentation .
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class FanMathBlock extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  def runDslCode(): Unit = {

    // 4096 - (($in - 110) * $gain);      // Input = pulse counter, speed gain = 50
    val in = Constant(uint32(0)).out      // Simulate the input

    val cst1 = Constant(uint32(4096)).out
    val cst2 = Constant(uint32(110)).out
    val cst3 = Constant(uint32(50)).out   // Gain

    val sub1 = Sub2(in, cst2).out
    val mul1 = Mul2(sub1, cst3).out
    val res = Sub2(cst1, mul1).out        // Result

    val not = Not(res)
  }

  runDotGeneratorTest()
}
