package hevs.especial.doc

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.{CFct, Constant, Mux2}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.generator.STM32TestSuite

/**
 * Simple application to demonstrate how the [[hevs.especial.generator.Resolver]] block works.
 *
 * Read a button, invert its state and convert it value to an [[uint8]] to control the selection pin of a [[Mux2]].
 * The `led1` is always ON. When the `btn1` is pressed, the `led2` switch OFF. Application without warnings.
 *
 * @version 1.0
 * @author Christopher Métrailler (mei@hevs.ch)
 */
class DemoResolver extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  import hevs.especial.dsl.components.CType.Implicits._

  def runDslCode(): Unit = {
    val not = Not() // Not and `uint8` conversion
    Stm32stkIO.btn1.out --> not.in

    val mux = Mux2[bool]()
    val cst1 = Constant[bool](true).out
    mux.out --> Stm32stkIO.led1.in  // Update outputs
    cst1 --> Stm32stkIO.led2.in

    not.out --> mux.sel
    Constant[bool](false).out --> mux.in2
    cst1 --> mux.in1
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