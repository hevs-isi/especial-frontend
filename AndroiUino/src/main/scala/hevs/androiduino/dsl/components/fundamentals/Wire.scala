package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging

import scala.reflect.runtime.universe._

// A <:< T in  (implicit evidence: A =:= Int )


// TODO: use template generic ?
// FIXME: InputPort[CType] or InputPort[_] ??
class Wire(val from: OutputPort[_], val to: InputPort[_]) extends Logging {

  //def getType[T <: CType : TypeTag](obj: T) = typeOf[T]

  //info("Creating a wire from [" + from + " --> " + to + "]")
  //info("Between \"" + from.getOwnerId + "\" and \"" + to.getOwnerId + "\".")

	// FIXME This is maybe not the best way to do it
	// and it does not allow to make static type checking -> template ?
	val sameType =  from.getClass equals to.getClass // FIXME typeOf
  println("From: " + from.getClass)
  println("To: " + to.getClass)
  //println("Type: " + getType(from))
  //assert(sameType, s"Incompatible connection type between ID${from.getOwnerId} ($from) and ID${to.getOwnerId} ($to)")
}