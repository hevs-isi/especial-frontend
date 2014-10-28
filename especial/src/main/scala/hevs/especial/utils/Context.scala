package hevs.especial.utils

/**
 * Contains the context of the current execution. Use by all blocks of the pipeline.
 */
class Context(var progName: String) {

  /** Logger used by the pipeline blocks to report any error or information. */
  val log = new Logger
}
