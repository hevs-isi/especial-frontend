package hevs.androiduino.dsl.utils

class WireConnection(msg: String) extends RuntimeException(msg)

class ComponentNotFound(msg: String) extends RuntimeException(msg)

object ComponentNotFound {
  def apply(id: Int) = new ComponentNotFound(s"Component id $id not found !")
}