package hevs.especial.utils

import org.scalatest.{FunSuite, Matchers}

/**
 * Test the result and the type of some simple `Pipeline`.
 */
class PipelineTest extends FunSuite with Matchers {

  /** Pipeline context */
  val ctx = new Context(this.getClass.getSimpleName)

  class PipeA extends Pipeline[Int, Int] {
    override def run(ctx: Context)(i: Int) = i + 10
  }

  class PipeB extends Pipeline[Int, Float] {
    override def run(ctx: Context)(i: Int) = i / 2.0f
  }

  class PipeC extends Pipeline[Float, String] {
    override val name = "MyPipeC"

    override def run(ctx: Context)(f: Float) = String.valueOf(f)
  }

  // Pipeline under tests
  val pA = new PipeA
  val pB = new PipeB
  val pC = new PipeC

  test("run each block individually") {
    val resA = pA.run(ctx)(10)
    val resB = pB.run(ctx)(10)
    val resC = pC.run(ctx)(10)

    resA shouldBe an[java.lang.Integer]
    resA shouldBe 20
    resB shouldBe a[java.lang.Float]
    resB shouldBe 5
    resC shouldBe a[java.lang.String]
    resC shouldBe "10.0"
  }

  test("chain 2 blocks") {
    val d = pA -> pB
    ctx.log.info(s"Run '$d'.")

    val resD = d.run(ctx)(50)
    resD shouldBe a[java.lang.Float]
    resD shouldBe 30
  }

  test("chain 3 blocks") {
    val d = pA -> pB -> pC
    ctx.log.info(s"Run '$d'.")

    val resD = d.run(ctx)(70)
    resD shouldBe a[java.lang.String]
    resD shouldBe "40.0"
  }

  test("chain the same block") {
    val d = pA -> pA -> pA
    ctx.log.info(s"Run '$d'.")

    val resD = d.run(ctx)(70)
    resD shouldBe an[java.lang.Integer]
    resD shouldBe 100
  }
}
