package hevs.especial.utils

/**
 * Base pipeline class used to chain different operations together.
 *
 * A pipeline block run a function from an input type `I` that return an output type `O`.
 * Pipelines can be chains together (similar to the `andThen` method of Scala). A logger is used to report
 * information or error. If an error is reported, the pipeline just stop.
 *
 * Code adapted from:
 *  - [[http://stackoverflow.com/a/9606174/938081]]
 *  - [[https://gist.github.com/richdougherty/730e03fc865870c46724]]
 *  - [[https://github.com/epfl-lara/leon/blob/master/src/main/scala/leon/Pipeline.scala]]
 *
 * @tparam I input type of the run method
 * @tparam O output type of the run method
 */
abstract class Pipeline[-I, +O] {

  /**
   * Name of the pipeline block.
   * Block names added when blocks are connected together.
   */
  protected val name: Seq[String] = Seq(this.getClass.getSimpleName)

  /**
   * Get the name of the current pipeline block and not all the chain.
   * @return the name of the current block
   */
  def currentName = name.last

  /**
   * Execute the pipeline code of the block.
   * An exception is thrown when blocks are chained if an error occurs.
   *
   * @param ctx the context of the program with the logger
   * @param input the input value of the pipeline
   * @return the result of the pipeline
   */
  def run(ctx: Context)(input: I): O

  /**
   * Used to chain pipelines. Create a new Pipeline from these two. Compute the first pipeline and then the second
   * (like the `andThen` Scala function). Immediately stop the program if an error is reported to the logger.
   *
   * @param next pipeline to execute after the first one
   * @tparam F output of the second pipeline
   * @return the result of the two chained pipeline
   */
  def ->[F](next: Pipeline[O, F]): Pipeline[I, F] = new Pipeline[I, F] {

    // Chains names of blocks
    override val name = Pipeline.this.name ++ next.name

    def run(ctx: Context)(v: I): F = {
      // Run the first block of the chain
      val first: O = Pipeline.this.run(ctx)(v)
      ctx.log.terminateIfErrors(Pipeline.this)

      // Run second block of the chain with the result of the first one
      val second = next.run(ctx)(first)
      ctx.log.terminateIfErrors(this)
      second // Return the final result
    }
  }

  /**
   * Print the name of the current blocks or of several blocks if chained.
   * @return the name the pipeline chain
   */
  override def toString = name.mkString(" -> ")
}