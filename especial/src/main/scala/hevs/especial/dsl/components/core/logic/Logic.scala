package hevs.especial.dsl.components.core.logic

import hevs.especial.dsl.components._

case class And2() extends AbstractLogic(2, "&") with In2 {
  override val description = s"And2 gate"

  override val in1 = in(0)
  override val in2 = in(1)
}

case class And3() extends AbstractLogic(3, "&") with In3 {
  override val description = s"And3 gate"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
}

case class And4() extends AbstractLogic(4, "&") with In4 {
  override val description = s"And4 gate"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
  override val in4 = in(3)
}



case class Or2() extends AbstractLogic(2, "|") with In2 {
  override val description = s"Or2 gate"

  override val in1 = in(0)
  override val in2 = in(1)
}

case class Or3() extends AbstractLogic(3, "|") with In3 {
  override val description = s"Or3 gate"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
}

case class Or4() extends AbstractLogic(4, "|") with In4 {
  override val description = s"Or4 gate"

  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
  override val in4 = in(3)
}