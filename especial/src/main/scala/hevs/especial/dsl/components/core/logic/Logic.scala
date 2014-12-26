package hevs.especial.dsl.components.core.logic

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.math.MathOps

case class And2(inputs: OutputPort[bool]*) extends MathOps(2, "&", inputs: _*) with In2 {
  override val description = s"And$nbrIn gate"

  override val in1 = in(0)
  override val in2 = in(1)
}

case class And3(inputs: OutputPort[bool]*) extends MathOps(3, "&", inputs: _*) with In3 {
  override val description = s"And$nbrIn gate"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
}

case class And4(inputs: OutputPort[bool]*) extends MathOps(4, "&", inputs: _*) with In4 {
  override val description = s"And$nbrIn gate"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
  override val in4 = in(3)
}


case class Or2(inputs: OutputPort[bool]*) extends MathOps(2, "|", inputs: _*) with In2 {
  override val description = s"Or$nbrIn gate"

  override val in1 = in(0)
  override val in2 = in(1)
}

case class Or3(inputs: OutputPort[bool]*) extends MathOps(3, "|", inputs: _*) with In3 {
  override val description = s"Or$nbrIn gate"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
}

case class Or4(inputs: OutputPort[bool]*) extends MathOps(4, "|", inputs: _*) with In4 {
  override val description = s"Or$nbrIn gate"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
  override val in4 = in(3)
}