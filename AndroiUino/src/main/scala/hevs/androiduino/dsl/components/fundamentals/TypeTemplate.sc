import hevs.androiduino.dsl.components.fundamentals.{CType, uint8, uint1}
//class Woof[+T]()

class Outport[+S](val t : S){
  //val v : Woof[S] = new Woof
  //val l : List[S] = List.empty[S]
  //	var l : List[Woof[S]] = List.empty[Woof[S]]
}
object Prout extends App {
  //def getType[T: TypeTag](obj: T) = typeOf[T]

  val in1 = new Outport(uint1(true))
  val in2 = new Outport(uint8(0))
  val l : List[Outport[CType]]=  List(in1, in2)
  //println(l.map(_.t))
  def pouet[T <: Outport[_]](l : List[T]) = {
    for(port <- l){
      println("class is " + port.t.getClass.getSimpleName)
    }
  }

  pouet(l)
}