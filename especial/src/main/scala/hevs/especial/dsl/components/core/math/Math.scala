package hevs.especial.dsl.components.core.math

import hevs.especial.dsl.components._

import scala.reflect.runtime.universe._

case class Add2[T <: CType : TypeTag](inputs: OutputPort[T]*) extends MathOps[T](2, "+", inputs: _*) with In2 {
  override val description = s"Add$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
}

case class Add3[T <: CType : TypeTag](inputs: OutputPort[T]*) extends MathOps[T](3, "+", inputs: _*) with In3 {
  override val description = s"Add$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
}

case class Add4[T <: CType : TypeTag](inputs: OutputPort[T]*) extends MathOps[T](4, "+", inputs: _*) with In4 {
  override val description = s"Add$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
  override val in4 = in(3)
}


case class Sub2[T <: CType : TypeTag](inputs: OutputPort[T]*) extends MathOps[T](2, "-", inputs: _*) with In2 {
  override val description = s"Sub$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
}

case class Sub3[T <: CType : TypeTag](inputs: OutputPort[T]*) extends MathOps[T](3, "-", inputs: _*) with In3 {
  override val description = s"Sub$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
}

case class Sub4[T <: CType : TypeTag](inputs: OutputPort[T]*) extends MathOps[T](4, "-", inputs: _*) with In4 {
  override val description = s"Sub$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
  override val in4 = in(3)
}


case class Mul2[T <: CType : TypeTag](inputs: OutputPort[T]*) extends MathOps[T](2, "*", inputs: _*) with In2 {
  override val description = s"Mul$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
}


case class Div2[T <: CType : TypeTag](inputs: OutputPort[T]*) extends MathOps[T](2, "/", inputs: _*) with In2 {
  override val description = s"Div$nbrIn"

  override val in1 = in(0)
  override val in2 = in(1)
}


