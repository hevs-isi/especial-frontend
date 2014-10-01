package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging

// TODO: use template generic ?
// FIXME: InputPort[CType] or InputPort[_] ??

@Deprecated
class WireOld extends Logging {

  // FIXME do not work...
  type Z <: CType

  var from: OutputPort[Z] = null
  var to: InputPort[Z] = null

  def setFrom[T <: CType](f: OutputPort[Z]) = from = f
  def setTo[T <: CType](t: InputPort[Z]) = to = t

  // Check the type of the two `Port`s to be sure that they can be connected
  /*val sameTypes = from.isSameTypeAs(to)
  sameTypes match {
    case false => error("from and to are not the same type")
    case _ => println("Wire is OK !")
  }*/
}