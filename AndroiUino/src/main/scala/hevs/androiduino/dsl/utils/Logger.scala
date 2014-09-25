package hevs.androiduino.dsl.utils

import java.io.IOException

import grizzled.slf4j.Logging

object Logger extends Logging {

  def fatal(msg: String): Nothing = {
    error(msg)
    throw new IOException(msg)
  }

  def warn(msg: String): Nothing = {
    warn(msg)
  }
}