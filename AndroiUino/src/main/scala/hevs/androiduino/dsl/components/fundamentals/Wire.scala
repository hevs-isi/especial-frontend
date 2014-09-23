package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging
import scala.reflect.runtime.universe._

// A <:< T in  (implicit evidence: A =:= Int )
class Wire(val a: OutputPort, val b: InputPort) extends Logging {
	def getType[T: TypeTag](obj: T) = typeOf[T]
	info("Creating wire with types " + a.t.getClass() + " - " + b.t.getClass())

	// FIXME This is maybe not the best way to do it 
	// and it does not allow to make static type checking
	val t1 = a.t
	val t2 = b.t
	assert(t1 == t2, s"Incompatible connection type between ID${a.owner.id} ($t1) and ID${b.owner.id} ($t2)")
}