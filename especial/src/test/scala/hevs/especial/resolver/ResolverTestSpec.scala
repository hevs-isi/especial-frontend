package hevs.especial.resolver

import hevs.especial.dsl.components.fundamentals.hw_implemented
import hevs.especial.generator.Resolver
import hevs.especial.utils.{Context, Logger}
import org.scalatest.{FunSuite, Matchers}

/**
 * Helper methods for the `Resolver` test suite using the `ScalaTest` library.
 */
abstract class ResolverTestSpec extends FunSuite with Matchers {

  /** Pipeline context */
  var ctx = new Context(this.getClass.getSimpleName)
  var r: Resolver = null

  /**
   * Resolve the current graph and return the result of the resolver.
   * @return the result of the resolver
   */
  def testResolver(): Map[Int, Set[hw_implemented]] = {
    // Create a new logger and a new resolver for each tests
    r = new Resolver
    val res = r.run(ctx)("")

    ctx.log.terminateIfErrors()
    ctx.log.info("Result:\n" + res.mkString("\n"))
    res
  }
}