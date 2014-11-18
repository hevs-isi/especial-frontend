package hevs.especial.dsl.components.core.logic

import hevs.especial.dsl.components._

case class And2() extends AbstractLogic(2, "&") with In2 with Out1 with HwImplemented {
  override val description = s"And2 gate"

  val in1 = in(1)

  val in2 = in(2)
}

case class And3() extends AbstractLogic(3, "&") with In3 with Out1 with HwImplemented {
  override val description = s"And3 gate"

  val in1 = in(1)

  val in2 = in(2)

  val in3 = in(3)
}

case class And4() extends AbstractLogic(4, "&") with In4 with Out1 with HwImplemented {
  override val description = s"And4 gate"

  val in1 = in(1)

  val in2 = in(2)

  val in3 = in(3)

  val in4 = in(4)
}

case class Or2() extends AbstractLogic(2, "|") with In2 with Out1 with HwImplemented {
  override val description = s"Or2 gate"

  val in1 = in(1)

  val in2 = in(2)
}