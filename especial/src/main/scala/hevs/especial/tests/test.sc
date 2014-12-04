import hevs.especial.dsl.components.core.Constant
import hevs.especial.dsl.components.{Pin, bool, uint8}

object f {
  val a: Byte = 12
  val b: Int = 3
  a == b
  22 == 344
  val x: bool = bool(true)
  val c1 = Constant(bool(true))
  val c2 = Constant(uint8(42))
  val c3 = Constant(x)
  // val c3 = Constant(true) // Crash, why ?
  // val c4 = Constant[Boolean](false) // Crash, OK
  val b1 = bool(false)
  val b2 = bool(true)
  val b3 = uint8(254)
  println(b1, b2, b3)
  println(c1.out)
  val s = Set(1,3,4,5,6,22)
  c.getOrElse("") + "dd\n"
  // Tous les éléments de l doivent être dans s
  val ok = l.foldLeft(true) {
    (acc, id) => acc & s.contains(id)
  }
  val map = scala.collection.mutable.HashMap.empty[Int, Set[Int]]
  /* Pattern matching on Pin */
  val p = Pin('C', 12)
  var c: Option[String] = None //Some("test")
  map += (1 -> Set(1,5))
  var l = List(22)
  p match {
    case Pin('A', 12) => println("GPIOA.12")
    case Pin('C', pin) => println("port C " + pin)
    case _ => println("other")
  }

  val p2 = Pin("ABC", 10)
  println("Port: " + p2.port)
}