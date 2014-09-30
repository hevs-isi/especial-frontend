import hevs.androiduino.dsl.components.core.Constant
import hevs.androiduino.dsl.components.fundamentals._
import hevs.androiduino.dsl.components.fundamentals.ImplicitTypes._
object f {
  val a: Byte = 12
  val b: Int = 3
  a == b
  22 == 344
  val x: uint1 = true
  val c1 = Constant(uint1(true))
  val c2 = Constant(uint8(42))
  val c3 = Constant(x)
  // val c3 = Constant(true) // Crash, why ?
  // val c4 = Constant[Boolean](false) // Crash, OK
  val b1 = uint1(false)
  val b2 = uint1(true)
  val b3 = uint8(254)
  println(b1, b2, b3)
  println(c1.out)

  val led

}