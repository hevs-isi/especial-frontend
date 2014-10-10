package hevs.androiduino.dsl.utils


// http://stackoverflow.com/a/9606174/938081
// http://www.jayway.com/2011/10/04/scala-type-variances-part-two/

abstract class Pipeline[-I, +O] {

  val name: String

  // Return a function with input type I and result type O
  def produce: I => O

  def stats: String

  def ->[X](seg: Pipeline[_ >: O, X]): Pipeline[I, X] = {
    val func = this.produce
    val outerName = this.name
    new Pipeline[I, X] {
      val name = outerName + "." + seg.name

      def produce = func andThen seg.produce

      def stats = seg.stats
    }
  }
}

abstract class Source[+T] extends Pipeline[Unit, T] {
}

class RandomInteger extends Source[Int] {
  override val name = "randInt"

  def produce: Unit => Int = (x: Unit) => scala.math.round(scala.math.random.asInstanceOf[Float] * 10)

  def stats = "stateless"
}

class TimesTen extends Pipeline[Int, Int] {
  override val name = "times"

  private var count = 0

  def produce: Int => Int = (x: Int) => {
    count += 1
    x * 10
  }

  def stats = "called for " + count + " times"
}


object Main {
  def main(args: Array[String]) {

    val p = new RandomInteger() -> new TimesTen()

    for (i <- 0 to 10)
      println(p.produce())

    println(p.name)  // "randInt.times10"
    println(p.stats) // called for 11 times
  }
}