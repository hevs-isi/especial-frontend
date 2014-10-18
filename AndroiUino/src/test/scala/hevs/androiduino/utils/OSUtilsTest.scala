package hevs.androiduino.utils

import java.io.IOException

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.utils.{LoggerError, OSUtils}
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
    val valid = OSUtils.runWithBooleanResult(cmd)
    if (valid._1) {
      info("dot is installed.")
      info(valid._2)
    }
    else
      info("dot is not installed !")

    // Run a none valid command
    val res3 = OSUtils.runWithBooleanResult("d" + cmd)
    assert(!res3._1, "Should not be a valid command !")
  }
}