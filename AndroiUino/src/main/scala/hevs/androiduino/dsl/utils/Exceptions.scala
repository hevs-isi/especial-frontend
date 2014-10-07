package hevs.androiduino.dsl.utils

/**
 * Try to connect two `Ports` with different types. This is not allowed. The input and output types must be the same.
 * @param msg Exception details
 */
class PortTypeMismatch(msg: String) extends RuntimeException(msg)

/**
 * Try to connect two outputs to the same input. This is not allowed.
 * @param msg Exception details
 */
class PortInputShortCircuit(msg: String) extends RuntimeException(msg)

/**
 * The component was not found in the code or graph.
 * @param msg Exception details
 */
class ComponentNotFound(msg: String) extends RuntimeException(msg)