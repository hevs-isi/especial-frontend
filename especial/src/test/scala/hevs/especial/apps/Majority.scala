package hevs.especial.apps

import hevs.especial.dsl.components.core.logic.{Or3, Or2, And2}
import hevs.especial.dsl.components.target.stm32stk.Stm32stkIO
import hevs.especial.genenator.STM32TestSuite

/**
 * Three inputs majority logic circuit.
 *
 * Buttons 2 (A), 3 (B) and 4 (C) are the 3 inputs.
 * The led1 (OA) is `ON` if at least two button are pressed (majority indication, method 1).
 * The led2 (OB) is `ON` if at least two button are pressed (majority indication, method 2).
 *
 * A B C | O
 * ----- | -
 * 0 0 0 | 0
 * 0 0 1 | 0
 * 0 1 0 | 0
 * 0 1 1 | 1
 * 1 0 0 | 0
 * 1 0 1 | 1
 * 1 1 0 | 1
 * 1 1 1 | 1
 *
 * OA = AB + BC + AC
 * OB = B(A + C) + AC
 */
class Majority extends STM32TestSuite {

  def isQemuLoggerEnabled = false

  // TODO: add variadic constructors to all logic components
  // TODO: add boolean operators as syntactic sugar

  def runDslCode(): Unit = {

    val A = Stm32stkIO.btn1.out
    val B = Stm32stkIO.btn2.out
    val C = Stm32stkIO.btn3.out

    val OA = Stm32stkIO.led1.in // Output for method 1
    val OB = Stm32stkIO.led2.in // Output for method 2

    // OA = AB + BC + AC
    val andA1 = And2(A, B) // And2()
    // A --> andA1.in1
    // B --> andA1.in2

    val andA2 = And2()
    B --> andA2.in1
    C --> andA2.in2

    val andA3 = And2()
    A --> andA3.in1
    C --> andA3.in2

    val orA1 = Or3(andA1.out, andA2.out, andA3.out)
    /*andA1.out --> orA1.in1
    andA2.out --> orA1.in2
    andA3.out --> orA1.in3*/
    orA1.out --> OA


    // OB = B(A + C) + AC
    val andB1 = And2()
    A --> andB1.in1
    C --> andB1.in2

    val orB1 = Or2()
    A --> orB1.in1
    C --> orB1.in2

    val andB2 = And2()
    B --> andB2.in1
    orB1.out --> andB2.in2

    val orB2 = Or2()
    andB1.out --> orB2.in1
    andB2.out --> orB2.in2
    orB2.out --> OB
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
