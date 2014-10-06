package hevs.androiduino.resolver

import hevs.androiduino.dsl.components.fundamentals.{Component, hw_implemented}
import hevs.androiduino.dsl.generator.Resolver
import org.scalatest.{FunSuite, Matchers}

/**
 * Helper methods for the `Resolver` test suite.
 */
abstract class ResolverTestSpec extends FunSuite with Matchers {

  val r = Resolver

  /**
   * Resolve the current graph and return only components IDs to test if the resolver works correctly.
   * @return components IDs in the right order to resolve
   */
  def testResolverWithIDs(): Seq[Int] = toIDs(testResolver)

  /**
   * Resolve the current graph and return all components.
   * @return components IDs in the right order to resolve
   */
  def testResolver(): Seq[hw_implemented] = r.resolve()

  /**
   * Return only components IDs.
   * @param l list of components
   * @return IDs of the components
   */
  def toIDs(l: Seq[hw_implemented]) = l map (x => x.asInstanceOf[Component].getId)
}