package hevs.androiduino.dsl.components.logic

import hevs.androiduino.dsl.components.fundamentals._

object And {
  def apply(nbrInput: Int = 2) = new And(nbrInput)
}

class And(nbrInput: Int = 2) extends AbstractLogic(nbrInput, "&") with hw_implemented {
  override val description = s"and$nbrInput gate"
}

object Or {
  def apply(nbrInput: Int = 2) = new Or(nbrInput)
}

class Or(nbrInput: Int = 2) extends AbstractLogic(nbrInput, "|") with hw_implemented {
  override val description = s"or$nbrInput gate"
}