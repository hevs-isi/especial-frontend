package hevs.especial.dsl.components

/**
 * A wire is used as an edge label in the graph to identify connections between ports.
 *
 * This is a container class, basically a [[Tuple2]] (but they cannot be override).
 * [[from]] and [[to]] identifies the connection source and destination.
 * Wires port types must be checked before creating the [[Wire]].
 *
 * @param from output port from
 * @param to input port to
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Wire(val from: OutputPort[CType], val to: InputPort[CType]) {

  /**
   * Equals two [[Wire]]s.
   * Check if [[Component]]s ID are the same.
   *
   * @param other object to equals
   * @return `true` if source and destination components ID are the same, `false` otherwise.
   */
  override def equals(other: Any) = other match {
    case that: Wire => that.from == from && that.to == to
    case _ => false
  }

  override def hashCode = 41 * from.hashCode() + to.hashCode()

  override def toString = {
    // Print the connection. Used by the [[DotGenerator]] and [[CycleException]].
    from + " --> " + to
  }
}