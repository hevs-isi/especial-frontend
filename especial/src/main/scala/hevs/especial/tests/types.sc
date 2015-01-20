import hevs.especial.dsl.components._

import hevs.especial.dsl.components.ImplicitTypes._
import hevs.especial.dsl.components.core.Constant

import scala.collection.mutable.ListBuffer

val b: bool = true

val c = Constant(b)
val s = c.getOutputs.get.head
println("Value: " + s)
val list = ListBuffer.empty[CType]
list += bool(v = true)
list += uint8(128)
list += int32(0xFFFF)
println("List is: " + list)
val list2 = ListBuffer.empty[InputPort[CType]]
list2 += new InputPort[bool](c) {
  override def setInputValue(s: String): String = ""

  override val name: String = "bool"
}

list2 += new InputPort[uint8](c) {

  override def setInputValue(s: String): String = ""

  override val name: String = "uint8"
}

println("List2 is: " + list2)
println("bool: "+ list2(0).getTypeAsString)
println("bool: "+ list2(1).getTypeAsString)
val o :Option[Seq[Int]] = Some(Seq(1,2,3))
println("Size1: " + o.getOrElse(Nil).size)
val p :Option[Seq[Int]] = None
println("Size1: " + p.getOrElse(Nil).size)


