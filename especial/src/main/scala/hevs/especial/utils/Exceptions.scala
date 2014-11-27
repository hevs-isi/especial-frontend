package hevs.especial.utils

import hevs.especial.dsl.components.{CType, Port}

object PortTypeMismatch {
  /** Format the exception message. */
  def create(from: Port[_], to: Port[_]) = {
    // Construct the error message
    val res = new StringBuilder
    res ++= "Ports types mismatch. Connection error !\n"
    res ++= s"Cannot connect the output `${from.name}` (type `${from.getTypeAsString}`) of ${from.getOwner}"
    res ++= s" to the input `${to.name}` (type `${to.getTypeAsString}`) of ${to.getOwner}."
    new PortTypeMismatch(res.result())
  }
}

/**
 * Try to connect two `Ports` with different types. This is not allowed. The input and output types must be the same.
 * @param msg exception details
 */
class PortTypeMismatch(msg: String) extends RuntimeException(msg)

object PortInputShortCircuit {
  /** Format the exception message. */
  def create(in: Port[_]) = {
    val error = s"Short circuit !\nThe input '${in.name}' of ${in.getOwner} is already connected."
    new PortInputShortCircuit(error)
  }
}

/**
 * Try to connect two outputs to the same input. This is not allowed.
 * @param msg exception details
 */
class PortInputShortCircuit(msg: String) extends RuntimeException(msg)

/**
 * The component was not found in the code or graph.
 * @param msg exception details
 */
class ComponentNotFound(msg: String) extends RuntimeException(msg)

object ComponentNotFound {
  /** Format the exception message. */
  def create(cpId: Int) = {
    new ComponentNotFound(s"Component id $cpId not found !")
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