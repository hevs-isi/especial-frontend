package hevs.especial.dsl.components.core

import hevs.especial.dsl.components.{bool, OutputPort}

/**
 * ==Logic==
 *
 * Provides classes to deals with [[hevs.especial.dsl.components.bool]] operations.
 *
 * Logic gates are available for multiples [[hevs.especial.dsl.components.bool]] inputs. All gates have only one
 * output. Logic gates can be easily connected using a variadic constructor.
 *
 * Here is a simple example:
 * {{{
 *  val cst1 = Constant[uint16](4096).out
 *  val cst2 = Constant[uint16](2).out
 *
 *  val tmp1 = Div2(cst1, cst2)
 *  tmp1.out --> Stm32stkIO.pwm4.in
 * }}}
 *
 * An implicit conversion is available to use boolean operators directly with boolean output ports (see
 * [[hevs.especial.dsl.components.core.logic.BooleanOutputPort]]). You must import:
 * {{{
 * import hevs.especial.dsl.components.core.logic._
 *
 * val A = Stm32stkIO.btn1.out
 * val B = Stm32stkIO.btn2.out
 * val C = Stm32stkIO.btn3.out
 * val O = Stm32stkIO.led1.in
 *
 * (A & B | B & C | A & C) --> O // Majority function
 * }}}
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
package object logic {

  // Use implicit conversions feature of the Scala language
  import scala.language.implicitConversions

  /**
   * Implicit conversion of a [[bool]] [[OutputPort]] to a rich [[BooleanOutputPort]].
   * @param out the boolean output port to convert
   * @return a rich [[BooleanOutputPort]]
   */
  implicit def toBooleanOutputPort(out: OutputPort[bool]): BooleanOutputPort = {
    new BooleanOutputPort(out)
  }
}