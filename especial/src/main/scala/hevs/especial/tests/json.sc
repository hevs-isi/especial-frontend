import hevs.especial.dsl.components.Pin
import hevs.especial.simulation._

val id2 = PeriphId(0)
id2 match {
  case DigitalOut => println("out")
  case DigitalIn => println("in")
  case PeriphId(id) => println("other: " + id)
}



val f = Pin('A', 12)

f match {
  case Pin('A', 12) => println("A12")
  case Pin('A', _) => println("A")
}

val p = PeriphId(43)
val c = new Command(56, f, 0)
val d = new Command(p, f, 1)


decodeJson("{\"periph\":0,\"pin\":{\"port\":\"C\",\"nbr\":12},\"value\":1}")
def decodeJson(jsonStr: String): Unit = {
  import net.liftweb.json.DefaultFormats
  import net.liftweb.json.JsonParser._
  implicit val formats = DefaultFormats // Brings in default date formats etc.
  val json = parse(jsonStr)
  val cmd = json.extract[Command]
  println("CMD: " + cmd)
  println("CMD: " + cmd.id)
  println("CMD: " + cmd.pin)
  println("CMD: " + cmd.value)

  cmd.id match {
    case DigitalOut => println("OUT**")
    case DigitalIn => println("IN**")
    case PeriphId(x) => println("Unknown id " + x)
  }

  println("finish")

  trait MyTrait {
    def test() = {
      println("test")
    }

    override def toString = "MyTrait"
  }

  case class MyClass() {
    override def toString = "MyClass"
  }

  println(new MyClass)
  println(new MyClass with MyTrait)


}