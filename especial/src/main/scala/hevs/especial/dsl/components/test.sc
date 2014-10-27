import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.fundamentals.{uint1, uint8}

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
  var c: Option[String] = None //Some("test")
  c.getOrElse("") + "dd\n"

  val s = Set(1,3,4,5,6,22)
  var l = List(22)

  // Tous les éléments de l doivent être dans s
  val ok = l.foldLeft(true) {
    (acc, id) => acc & s.contains(id)
  }
  val map = scala.collection.mutable.HashMap.empty[Int, Set[Int]]
  map += (1 -> Set(1,5))

  class MyClass {
    /*override def toString = {
      //"miaou"
      //???
    }*/
  }
  val instanceName = new MyClass()
  println(instanceName) // Should print "instanceName"

}