package hevs.androiduino.utils

import hevs.androiduino.dsl.utils.{Logger, Pipeline}
import org.scalatest.{FunSuite, Matchers}

/**
 * Test the result and the type of some simple `Pipeline`.
 */
class PipelineTest extends FunSuite with Matchers {

  val logger = new Logger

  class PipeA extends Pipeline[Int, Int] {
    override val name = "PipeA"

    override def run(log: Logger)(i: Int) = i + 10
  }

  class PipeB extends Pipeline[Int, Float] {
    override val name = "PipeB"

    override def run(log: Logger)(i: Int) = i / 2.0f
  }

  class PipeC extends Pipeline[Float, String] {
    override val name = "PipeC"

    override def run(log: Logger)(f: Float) = String.valueOf(f)
  }

  // Pipeline under tests
  val pA = new PipeA
  val pB = new PipeB
  val pC = new PipeC

  test("run individually") {
    val resA = pA.run(logger)(10)
    val resB = pB.run(logger)(10)
    val resC = pC.run(logger)(10)

    resA shouldBe an[java.lang.Integer]
    resA shouldBe 20
    resB shouldBe a[java.lang.Float]
    resB shouldBe 5
    resC shouldBe a[java.lang.String]
    resC shouldBe "10.0"
  }

  test("chain 2 blocks") {
    val d = pA -> pB
    val resD = d.run(logger)(50)

    resD shouldBe a[java.lang.Float]
    resD shouldBe 30
  }

  test("chain 3 blocks") {
    val d = pA -> pB -> pC
    val resD = d.run(logger)(70)

    resD shouldBe a[java.lang.String]
    resD shouldBe "40.0"
  }

  test("chain same") {
    val d = pA -> pA -> pA
    val resD = d.run(logger)(70)

    resD shouldBe an[java.lang.Integer]
    resD shouldBe 100
  }
}
