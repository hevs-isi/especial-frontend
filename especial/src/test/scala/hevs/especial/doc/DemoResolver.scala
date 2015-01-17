package hevs.especial.doc

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.{Mux2, CFct, Constant, Mux3}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.generator.STM32TestSuite

/**
 * Code used for the documentation (report) only.
 *
 * Simple application to demonstrate how the resolver works. Application without warnings.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class DemoResolver extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  import hevs.especial.dsl.components.CType.Implicits._

  def runDslCode(): Unit = {
    // Inputs
    val cst1 = Constant[bool](true).out
    val cst2 = Constant[bool](false).out
    val btn1 = Stm32stkIO.btn1.out

    // Logic
    val mux2 = Mux2[bool]()
    val not = Not()

    // Outputs
    val led1 = Stm32stkIO.led1.in
    val led2 = Stm32stkIO.led2.in

    // Connecting stuff
    cst1 --> mux2.in1
    cst2 --> mux2.in2

    // bool to uint8 conversion to control the Mux2 selection pin
    btn1 --> not.in
    not.out --> mux2.sel

    mux2.out --> led1
    mux2.out --> led2
  }

  case class Not() extends CFct[bool, uint8] {
    override val description = s"NOT gate"
    private val convValue = outValName()

    /* I/O management */
    override def getOutputValue: String = convValue

    /* Code generation */
    override def loopCode = s"${uint8().getType} $convValue = if($getInputValue) ? 0 : 1;"
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}