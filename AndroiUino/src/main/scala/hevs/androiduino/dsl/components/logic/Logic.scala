package hevs.androiduino.dsl.components.logic

import hevs.androiduino.dsl.components.fundamentals._
import hevs.androiduino.dsl.components.{In2, In3, In4, Out1}

case class And2() extends AbstractLogic(2, "&") with In2 with Out1 with hw_implemented {
  override val description = s"And2 gate"

  def in1 = in(1)

  def in2 = in(2)
}

case class And3() extends AbstractLogic(3, "&") with In3 with Out1 with hw_implemented {
  override val description = s"And3 gate"

  def in1 = in(1)

  def in2 = in(2)

  def in3 = in(3)
}

case class And4() extends AbstractLogic(4, "&") with In4 with Out1 with hw_implemented {
  override val description = s"And4 gate"

  def in1 = in(1)

  def in2 = in(2)

  def in3 = in(3)

  def in4 = in(4)
}

case class Or2() extends AbstractLogic(2, "|") with In2 with Out1 with hw_implemented {
  override val description = s"Or2 gate"

  def in1 = in(1)

  def in2 = in(2)
}