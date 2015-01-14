package hevs.especial.simulation

import hevs.especial.dsl.components.Pin
import hevs.especial.generator.VcdGenerator
import hevs.especial.utils.Context
import org.scalatest.FunSuite

/**
 * Generate a predefined output sequence to a VCD file.
 *
 * The generated file can be opened with any vcd viewer to view the result graphically.
 * The `impulse` (http://toem.de/index.php/projects/impulse) Eclipse plugin is one available viewer.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class VcdGeneratorTest extends FunSuite {

  test("Predefined VCD generator") {

    val pins: Map[Pin, Seq[Int]] = Map(
      Pin('A', 1) -> Seq(0, 1, 0, 1),
      Pin('B', 2) -> Seq(1, 0, 1, 0),
      Pin('C', 3) -> Seq(0, 1, 1, 0),
      Pin('D', 4) -> Seq(0, 1, 0)
    )

    // Generate the VCD file
    val ctx = new Context(this.getClass.getSimpleName, true)
    val vcdGen = new VcdGenerator().run(ctx)(pins)
    assert(vcdGen, "Unable to generate the VCD file !")

    // Check if errors have been reported or not
    assert(!ctx.log.hasWarnings)
    assert(!ctx.log.hasErrors)
  }
}
