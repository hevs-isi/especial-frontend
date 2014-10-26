package hevs.androiduino.resolver

import hevs.androiduino.dsl.components.fundamentals.{Component, hw_implemented}
import hevs.androiduino.dsl.generator.Resolver
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