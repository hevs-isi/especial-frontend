package hevs.especial.generator

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.{CFct, Constant, Mux3}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO

/**
 * A Mux is used to control `led1` and `led2`.
 * When the `btn1` is pressed, outputs LEDs are OFF, and ON otherwise.
 *
 * For this test, a [[Mux3]] is used. The third input is disconnected (not used). A waring will be found during the
 * code generation. This input value is set to `0` (false) when not used.
 *
 * A custom component is used to convert the boolean value of the button to an [[uint8]] value to control the
 * [[Mux3]] selection pin.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Sch3Code extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  import hevs.especial.dsl.components.CType.Implicits._

  def runDslCode(): Unit = {
    // Inputs
    val cst1 = Constant[bool](true).out
    val cst2 = Constant[bool](false).out
    val btn1 = Stm32stkIO.btn1.out

    // Logic
    val mux2 = Mux3[bool]()
    val conv = FromBool[uint8]()

    // Outputs
    val led1 = Stm32stkIO.led1.in
    val led2 = Stm32stkIO.led2.in

    // Connecting stuff
    cst1 --> mux2.in1
    cst2 --> mux2.in2

    // bool to uint8 conversion to control the Mux2 selection pin
    btn1 --> conv.in
    conv.out --> mux2.sel

    mux2.out --> led1
    mux2.out --> led2

    // This will create a cycle and is NOT permitted
    // mux2.out --> mux2.in3
  }

  import scala.reflect.runtime.universe._

  /**
   * Custom component to convert a [[bool]] input to any output type.
   * If the input is `true`, the output is '1', otherwise '0'.
   *
   * @tparam T the output type
   */
  case class FromBool[T <: CType : TypeTag]() extends CFct[bool, T] {

    private val convValue = valName("conv")

    /* I/O management */
    override def getOutputValue: String = {
      // Return the conversion result stored in a local variable
      convValue
    }

    /* Code generation */
    override def loopCode = {
      val outType = getTypeString[T]
      val in = getInputValue
      s"""
         |$outType $convValue = 0;
         |if($in == true)
         |  $convValue = 1;
      """.stripMargin
    }
  }

  runDotGeneratorTest()

  runCodeCheckerTest(hasWarnings = true)

  runCodeOptimizer(hasWarnings = true)

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}