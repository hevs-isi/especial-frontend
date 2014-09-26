import hevs.androiduino.dsl.components.fundamentals._
object Test {
  println("Hello World 1 !")
  // val a = 42
  val b: CType = uint1(true)
  println("b is: " + b)
  println("b bool: " + b.asBool)
  println("b int: " + b.asLong)
  println("b int: " + b.getType)
  val c = b.asBool
  val d = b.asLong
  // val e = b.asString // not implemented
  val e = uint8(255)
  val f = uint16(65535)
  println("OK")
  val g = int16(-1).asLong
  val h = float(-14.25f)
  h.asLong // crop: -14
  h.getType
  double(1).getType
  double(1.256).asLong // crop: 1
  import ImplicitTypes._
  val lg: Long = uint16(1850)
  val toto = uint16(lg.toInt)
}