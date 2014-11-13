package hevs.especial.genenator

import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}
import hevs.especial.simulation.QemuLogger

class Sch1Code extends STM32TestSuite {

  def getDslCode = {

    // Define the target with the QEMU debug option
    new Stm32stk with QemuLogger

    // Inputs
    val btn1 = DigitalInput(Stm32stk.p_btn)
    // val cst1 = Constant(uint1(v = false))

    // Outputs
    val led1 = DigitalOutput(Stm32stk.p_led)

    // Connecting stuff
    btn1.out --> led1.in
    // cst1.out --> led1.in
  }

  runDotGeneratorTest()
  runCodeCheckerTest(hasWarnings = false)

  runCodeGenTest()
}