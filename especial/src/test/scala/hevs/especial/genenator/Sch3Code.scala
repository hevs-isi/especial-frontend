package hevs.especial.genenator

import hevs.especial.dsl.components.core.{Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.{DigitalInput, DigitalOutput, Stm32stk}
import hevs.especial.dsl.components.{Pin, bool}

class Sch3Code extends STM32TestSuite {

  def isQemuLoggerEnabled = true

  def runDslCode(): Unit = {

    import hevs.especial.dsl.components.ImplicitTypes._

    val b: bool = true

    // Inputs
    val cst1 = Constant[bool](b) // !b is not available
    val cst2 = Constant[bool](false) // Constant(bool(v = false))
    val btn1 = DigitalInput(Stm32stk.pin_btn)

    // Logic
    val mux2 = Mux2[bool]()

    // Output
    val led1 = DigitalOutput(Stm32stk.pin_led)
    val led2 = DigitalOutput(Pin('C', 0xD))

    // Connecting stuff
    cst1.out --> led1.in
    cst1.out --> mux2.in1
    cst2.out --> mux2.in2

    /**
     * Ports types mismatch. Connection error !
     * Cannot connect the output `out` (type `bool`) of Cmp[3] 'DigitalInput' to
     * the input `sel` (type `uint8`) of Cmp[4] 'Mux2'.
     */
    btn1.out --> mux2.sel // Type error. Not detected at the compilation time ?

    mux2.out --> led1.in
    mux2.out --> led2.in
  }

  runDotGeneratorTest(optimizedVersion = false)

  runCodeCheckerTest(hasWarnings = true)

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}