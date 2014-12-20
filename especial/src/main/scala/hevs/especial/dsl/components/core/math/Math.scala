package hevs.especial.dsl.components.core.math

import hevs.especial.dsl.components.{In2, CType}

import scala.reflect.runtime.universe._

/**
 * Compute standard math operations from a generic number of inputs.
 *
 * Compute the addition of a generic number of inputs and produce one result in the same format as the input.
 * Warning: inputs and outputs have the same type, check overflows !
 *
 * @tparam T Inputs types
 */
abstract class MathOps[T <: CType : TypeTag](val nbrIn: Int, operator: String) extends AbstractMath[T](nbrIn) {

  override protected def getOutputValue: String = {
    // The output is the addition of all inputs
    val inputs = for (i <- 0 until nbrIn) yield inValName(i)
    inputs.mkString(s" $operator ")
  }
}

// TODO: add variadic constructor like logic when tested

// TODO: add more math blocks (modulo, min, max, shift, etc...)

case class Add2[T <: CType : TypeTag]() extends MathOps[T](2, "+") with In2 {

  override val description = s"Add$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
}

case class Sub2[T <: CType : TypeTag]() extends MathOps[T](2, "-") with In2 {

  override val description = s"Sub$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
}

case class Mul2[T <: CType : TypeTag]() extends MathOps[T](2, "*") with In2 {

  override val description = s"Mult$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
}

case class Div2[T <: CType : TypeTag]() extends MathOps[T](2, "/") with In2 {

  override val description = s"Div$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
}


