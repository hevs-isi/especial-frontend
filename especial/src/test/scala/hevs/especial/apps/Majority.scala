package hevs.especial.apps

import hevs.especial.dsl.components.core.logic.{And2, Or2, Or3}
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

  // TODO: add boolean operators as syntactic sugar

  def runDslCode(): Unit = {

    // Inputs
    val A = Stm32stkIO.btn1.out
    val B = Stm32stkIO.btn2.out
    val C = Stm32stkIO.btn3.out

    // Output
    val OA = Stm32stkIO.led1.in

    // First method
    // OA = AB + BC + AC
    val andA1 = And2(A, B).out
    val andA2 = And2(B, C).out
    val andA3 = And2(A, C).out
    Or3(andA1, andA3, andA2).out --> OA

    // Second method
    // OB = B(A + C) + AC
    /*val andB1 = And2()
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
    orB2.out --> OB*/
  }

  runDotGeneratorTest()

  runCodeCheckerTest()

  runCodeOptimizer()

  runDotGeneratorTest(optimizedVersion = true)

  runCodeGenTest()
}
