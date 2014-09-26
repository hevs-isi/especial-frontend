package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging

import scala.reflect.runtime.universe._

// A <:< T in  (implicit evidence: A =:= Int )

// TODO: use template generic ?
class Wire(val a: OutputPort[_], val b: InputPort[_]) extends Logging {

  def getType[T: TypeTag](obj: T) = typeOf[T]

  info("Creating a wire from [" + a + " --> " + b + "]")
  info("Between \"" + a.getOwnerId + "\" and \"" + b.getOwnerId + "\".")

	// FIXME This is maybe not the best way to do it
	// and it does not allow to make static type checking -> template ?
	val sameType =  a.getClass equals b.getClass // FIXME typeOf
  assert(sameType, s"Incompatible connection type between ID${a.getOwnerId} ($a) and ID${b.getOwnerId} ($b)")
}