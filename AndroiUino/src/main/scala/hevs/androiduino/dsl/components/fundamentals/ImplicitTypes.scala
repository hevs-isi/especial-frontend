package hevs.androiduino.dsl.components.fundamentals

import scala.language.implicitConversions

/**
 * Implicit conversions for `fundamentals.CType`.
 */
object ImplicitTypes {
  implicit def uint1ToLong(v: uint1) = v.asLong

  implicit def uint8ToLong(v: uint8) = v.asLong

  implicit def uint16ToLong(v: uint16) = v.asLong

  implicit def uint32ToLong(v: uint32) = v.asLong

  implicit def int8ToLong(v: int8) = v.asLong

  implicit def int16ToLong(v: int16) = v.asLong

  implicit def int32ToLong(v: int32) = v.asLong

  implicit def floatToFloat(v: float) = v.asFloat

  implicit def doubleToFloat(v: double) = v.asFloat

  // TODO test and add implicit conversions
}
