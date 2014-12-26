package hevs.especial.dsl.components.core

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
 */
package object logic {

}