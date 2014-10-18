package hevs.androiduino.dsl.utils

/**
 * Try to connect two `Ports` with different types. This is not allowed. The input and output types must be the same.
 * @param msg exception details
 */
class PortTypeMismatch(msg: String) extends RuntimeException(msg)

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