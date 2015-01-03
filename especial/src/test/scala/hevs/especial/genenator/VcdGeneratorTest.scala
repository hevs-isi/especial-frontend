package hevs.especial.genenator

import hevs.especial.dsl.components.Pin
import hevs.especial.generator.VcdGenerator
import hevs.especial.utils.Context
import org.scalatest.FunSuite

/**
 * Generate a predefined output sequence to a VCD file.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class VcdGeneratorTest extends FunSuite {

  val gen = new VcdGenerator()

  test("Predefined VCD generator") {

    val ctx = new Context("vcdTest", true)

    val pins: Map[Pin, Seq[Int]] = Map(
      Pin('A', 1) -> Seq(0, 1, 0, 1),
      Pin('B', 2) -> Seq(1, 0, 1, 0),
      Pin('C', 3) -> Seq(0, 1, 1, 0),
      Pin('D', 4) -> Seq(0, 1, 0)
    )

    gen.run(ctx)(pins) // Generate the VCD file

    // Check if errors have been reported or not
    assert(!ctx.log.hasWarnings)
    assert(!ctx.log.hasErrors)
  }
}
