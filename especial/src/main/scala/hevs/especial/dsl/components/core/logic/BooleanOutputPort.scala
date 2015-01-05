package hevs.especial.dsl.components.core.logic

import hevs.especial.dsl.components.{OutputPort, bool}

/**
 * Rich [[OutputPort]] which add operations on [[bool]] [[OutputPort]]s.
 *
 * This is a wrapper around a boolean output port. It add functions to work directly with boolean operations.
 * Logic components are used automatically. `AND`, `OR` and `NOT` operations are supported.
 *
 * @param port the boolean output port to use
 *
 * @version 1.1
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class BooleanOutputPort(port: OutputPort[bool]) {

  /** Add operations of boolean output ports. */
  private type T = OutputPort[bool]

  /** And operator, see [[And2]]. */
  def &(out: T): T = And2(port, out).out

  /** And operator, see [[Or2]]. */
  def |(out: T): T = Or2(port, out).out

  /** Not operator, see [[Not]]. */
  def unary_!(): T = Not(port).out
}
