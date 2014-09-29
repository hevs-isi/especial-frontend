package ch.hevs.hellograph.dot

object Bench {

  /**
   * Microbenchmarking is not the best way to do it
   * but still. Answer in milliseconds
   */
  def microBenchmark(nRuns: Integer = 20, display: Boolean = true)(f: ⇒ Unit): Double = {
    var length: Long = 0

    for (i ← 0 until nRuns) {
      val now = System.currentTimeMillis

      { f } // Execute f

      val run = System.currentTimeMillis - now
      length = length + run
    }

    if (display == true) {
      print("[uBench] Run time " + length + " ms")

      if (nRuns >= 1)
        println(" / Per it. " + length / nRuns + " ms on average");
      else
        println()
    }

    length
  }

}