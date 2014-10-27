package hevs.especial.resolver

import hevs.especial.dsl.components.fundamentals.hw_implemented
import hevs.especial.generator.Resolver
import org.scalatest.{FunSuite, Matchers}

/**
 * Helper methods for the `Resolver` test suite using the `ScalaTest` library.
 */
abstract class ResolverTestSpec extends FunSuite with Matchers {

  val r = Resolver

  /**
   * Resolve the current graph and return the result of the resolver.
   * @return the result of the resolver
   */
  def testResolver(): Map[Int, Set[hw_implemented]] = r.resolve()
}