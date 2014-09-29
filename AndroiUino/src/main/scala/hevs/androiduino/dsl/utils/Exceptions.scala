package hevs.androiduino.dsl.utils

import grizzled.slf4j.Logging

case class WireException(msg: String) extends Exception with Logging {
  error("Unable to create this wire.")
}