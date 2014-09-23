package hevs.androiduino.dsl.components
import scala.reflect.runtime.universe._

object f {
  val a: Byte = 12                                //> a  : Byte = 12
  val b: Int = 3                                  //> b  : Int = 3
  a == b                                          //> res0: Boolean = false
  22 == 344                                       //> res1: Boolean(false) = false
}
		