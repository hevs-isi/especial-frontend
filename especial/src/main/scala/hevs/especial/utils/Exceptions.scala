package hevs.especial.utils

import hevs.especial.dsl.components.{Component, Port}

/**
 * Try to connect two `Ports` with different types. This is not allowed. The input and output types must be the same.
 * @param msg exception details
 */
class PortTypeMismatch(msg: String) extends RuntimeException(msg)

object PortTypeMismatch {

  /**
   * Format the exception message.
   * @param from the port from
   * @param to the port to
   * @return the exception
   */
  def apply(from: Port[_], to: Port[_]) = {
    // Construct the error message
    val res = new StringBuilder
    res ++= "Ports types mismatch. Connection error !\n"
    res ++= s"Cannot connect the output `${from.name}` (type `${from.getTypeAsString}`) of ${from.getOwner}"
    res ++= s" to the input `${to.name}` (type `${to.getTypeAsString}`) of ${to.getOwner}."
    new PortTypeMismatch(res.result())
  }
}


/**
 * Try to use the same input or output with two different functions. This is not allowed.
 * When an I/O is configured, its type cannot be changed.
 * @param msg exception details
 */
class IoTypeMismatch(msg: String) extends RuntimeException(msg)

object IoTypeMismatch {

  /**
   * Format the exception message.
   * @param extCmp the current existing component in the graph
   * @param newCmp the other component type (which is not allowed)
   * @return the exception
   */
  def apply(extCmp: Component, newCmp: Component) = {
    val curCmpType = extCmp.getClass.getSimpleName // Current component type
    val newCmpType = newCmp.getClass.getSimpleName // Other component type

    // Construct the error message
    val res = new StringBuilder
    res ++= "IO already used !\n"
    res ++= s"The component $extCmp is already used as '$curCmpType'.\n"
    res ++= s"Cannot be used as '$newCmpType'."
    new IoTypeMismatch(res.result())
  }
}


/**
 * Try to connect two outputs to the same input. This is not allowed.
 * @param msg exception details
 */
class PortInputShortCircuit(msg: String) extends RuntimeException(msg)

object PortInputShortCircuit {

  /**
   * Format the exception message.
   * @param in the port in short circuit
   * @return the exception
   */
  def apply(in: Port[_]) = {
    val error = s"Short circuit !\nThe input '${in.name}' of ${in.getOwner} is already connected."
    new PortInputShortCircuit(error)
  }
}


/**
 * The component was not found in the code or graph.
 * @param msg exception details
 */
class ComponentNotFound(msg: String) extends RuntimeException(msg)

object ComponentNotFound {

  /**
   * Format the exception message.
   * @param cpId the component id not found in the graph
   * @return the exception
   */
  def apply(cpId: Int) = {
    new ComponentNotFound(s"Component id '$cpId' not found !")
  }
}


/**
 * Try to run a third party executable or library, which is not supported by the host OS.
 * @param msg exception details
 */
class OsNotSupported(msg: String) extends RuntimeException(msg)


/**
 * The program terminates because one or more errors have occurred.
 * @param msg exception details
 */
class LoggerError(msg: String) extends RuntimeException(msg)


/**
 * A cycle has been found in the DAG graph. The current node or edge cannot be added.
 * @param msg exception details
 */
class CycleException(msg: String) extends IllegalArgumentException(msg)

object CycleException {

  import hevs.especial.dsl.components.Wire

  /**
   * Format the exception message.
   * @param wire the label of the edges (wire) which create the cycle
   * @return the exception
   */
  def apply(wire: Wire) = {
    val header = "Cycle found in the graph. This is not permitted. The graph must be a DAG !\n"
    val w = "Addition refused. Wire error: " + wire
    new CycleException(header + w)
  }
}