package hevs.androiduino.dsl.components
import scala.reflect.runtime.universe._
import hevs.androiduino.dsl.components.fundamentals.uint8
import hevs.androiduino.dsl.components.fundamentals.uint1
import hevs.androiduino.dsl.components.fundamentals.C_Types

//trait foo{}
//class Input_1(val v: Boolean = false) extends foo
//class Input_8(val v: Int = 0) extends foo

class Woof[+T]()

//class Outport[+S<:C_Types](val t : S){
class Outport[+S](val t : S){
	val v : Woof[S] = new Woof
	val l : List[S] = List.empty[S]
	
//	var l : List[Woof[S]] = List.empty[Woof[S]]
}

object Prout extends App {
	def getType[T: TypeTag](obj: T) = typeOf[T]
	
	val in1 = new Outport[uint1](uint1(true))
	val in2 = new Outport[uint8](uint8(0))
	val l : List[Outport[C_Types]]=  List(in1, in2)
	
	println(l.map(_.t))
	
	def pouet[T <: Outport[C_Types]](l : List[T]) = {
		for(port <- l){
			println(port.t.getClass())
		}
	}
	
	pouet(l)
}