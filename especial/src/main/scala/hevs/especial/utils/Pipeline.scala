package hevs.especial.utils

/**
 * Base pipeline class used to chain different operations together.
 *
 * A pipeline block `run` a function from an input type `I` that return an output type `O`. Pipelines can be
 * chains together (similar to the `andThen` method of Scala). A logger is used to report any info or error. If an
 * error is reported, the pipeline just stop.
 *
 * Code adapted from:
 * @see http://stackoverflow.com/a/9606174/938081
 * @see https://gist.github.com/richdougherty/730e03fc865870c46724
 * @see https://github.com/epfl-lara/leon/blob/master/src/main/scala/leon/Pipeline.scala
 *
 * @tparam I input type of the run method
 * @tparam O output type of the run method
 */
abstract class Pipeline[-I, +O] {

  /**
   * Name of the pipeline block. By default, it is the name of the class.
   */
  val name: String = this.getClass.getSimpleName

  /**
   * Execute the pipeline block.
   * @param log the logger used to report if any error occur
   * @param input the input value of the pipeline
   * @return the result of the pipeline
   */
  def run(log: Logger)(input: I): O

  /**
   * Used to chain pipelines. Create a new Pipeline from these two. Compute the first pipeline and then the second
   * (like the `andThen` Scala function).
   * @param next pipeline to execute after the first one
   * @tparam F output of the second pipeline
   * @return the result of the two chained pipeline
   */
  def ->[F](next: Pipeline[O, F]): Pipeline[I, F] = new Pipeline[I, F] {

    // Name of the chain of pipelines
    override val name = Pipeline.this.name + " -> " + next.name

    def run(log: Logger)(v: I): F = {
      val first: O = Pipeline.this.run(log)(v) // Run the first one
      log.terminateIfErrors()
      next.run(log)(first) // Run second with the result of the first one
    }
  }

  override def toString = name
}