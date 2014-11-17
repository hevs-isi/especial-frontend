package hevs.especial.genenator

import hevs.especial.dsl.components.Pin
import hevs.especial.generator.VcdGenerator
import hevs.especial.utils.Context


object VcdGeneratorTest extends App {

  val pins: Map[Pin, Seq[Int]] = Map(
    Pin('A', 1) -> Seq(0, 1, 0, 1),
    Pin('B', 2) -> Seq(1, 0, 1, 0),
    Pin('C', 3) -> Seq(0, 1, 1, 0),
    Pin('D', 4) -> Seq(1, 0, 0)
  )

  val ctx = new Context("vcdTest", false)
  new VcdGenerator().run(ctx)(pins)

}
