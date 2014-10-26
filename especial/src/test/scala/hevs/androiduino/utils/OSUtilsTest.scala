package hevs.androiduino.utils

import java.io.IOException

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.utils.OSUtils
import org.scalatest.FunSuite

class OSUtilsTest extends FunSuite with Logging {

  test("Check OS type") {
    info(s"Running on '${OSUtils.osName}'.")
    info(s"Is Linux: ${OSUtils.isLinux}.")
    info(s"Is Windows: ${OSUtils.isWindows}.")
    info(s"Is Other: ${OSUtils.isOther}.")
  }

  test("Exec commands") {
    val cmd = "dot -V"
    // Run a command to check if dot is installed
    val valid = OSUtils.runWithCodeResult(cmd)
    if (valid._1 == 0) {
      info("dot is installed.")
      info(OSUtils.runWithResult(cmd)) //same as valid._2
    }
    else
      info("dot is not installed !")

    // Run a none valid command
    val res3 = OSUtils.runWithCodeResult("d" + cmd)
    assert(res3._1 != 0, "Should not be a valid command !")
    info("Invalid command:")
    info(res3)

    intercept[IOException] {
      OSUtils.runWithResult("e" + cmd)
    }
  }
}