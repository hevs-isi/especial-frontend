package hevs.especial.genenator

import hevs.especial.generator.VcdGenerator
import hevs.especial.utils.Context


object VcdGeneratorTest extends App {

  val ctx = new Context("vcdTest", false)
  new VcdGenerator().run(ctx)(Unit)

}
