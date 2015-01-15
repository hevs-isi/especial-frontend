package hevs.especial.doc

import hevs.especial.utils.{Context, Pipeline}
import org.scalatest.{FunSuite, Matchers}

import scala.util.Random

/**
 * Code used for the documentation (report) only.
 * If the random number is odd:
 * {{
 * [INFO] Random number is 25
 * [ERROR] Fatal: 25 is not an even number !
 * }}
 * If the random number is even:
 * {{
 * [INFO] Random number is 76
 * [INFO] The result is 152.
 * }}
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class DemoPipe extends FunSuite with Matchers {

  case class Block1() extends Pipeline[Unit, Int] {
    override def run(ctx: Context)(i: Unit) = {
      val nbr = new Random().nextInt(100)
      ctx.log.info(s"Random number is $nbr"); nbr
    }
  }

  case class Block2() extends Pipeline[Int, Int] {
    override def run(ctx: Context)(i: Int) = i % 2 == 0 match {
      case true => 2 * i
      case _ => ctx.log.fatal(s"$i is not an even number !")
    }
  }

  case class Block3() extends Pipeline[Int, String] {
    override def run(ctx: Context)(i: Int) = s"The result is $i."
  }

  test("Chain of 3 blocks") {
    val pipe = Block1() -> Block2() -> Block3()
    val ctx = new Context("Demo")
    val res = pipe.run(ctx)(Unit) // Run the pipeline (no input required)
    ctx.log.info(res)
  }
}