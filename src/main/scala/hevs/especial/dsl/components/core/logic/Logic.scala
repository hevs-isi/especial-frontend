package hevs.especial.dsl.components.core.logic

import hevs.especial.dsl.components._
import hevs.especial.dsl.components.core.math.MathOps

object And {

  /**
   * Create an [[And]] gate with 0 to 4 inputs.
   * @param inputs gates inputs (connected automatically from in1 to in 4)
   * @return the component corresponding to the number of inputs (And2,3,4)
   */
  def apply(inputs: OutputPort[bool]*) = inputs.size match {
    case 0 | 1 | 2 => And2(inputs: _*)
    case 3 => And3(inputs: _*)
    case 4 => And4(inputs: _*)
  }
}

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

object Or {

  /**
   * Create an [[Or]] gate with 0 to 4 inputs.
   * @param inputs gates inputs (connected automatically from in1 to in 4)
   * @return the component corresponding to the number of inputs (Or2,3,4)
   */
  def apply(inputs: OutputPort[bool]*) = inputs.size match {
    case 0 | 1 | 2 => Or2(inputs: _*)
    case 3 => Or3(inputs: _*)
    case 4 => Or4(inputs: _*)
  }
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