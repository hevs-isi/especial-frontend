package hevs.androiduino.dsl.components.fundamentals

import scala.language.implicitConversions

/**
 * Implicit conversions for `fundamentals.CType`.
 */
object ImplicitTypes {

  // From CTypes to Scala
  implicit def uint1ToLong(v: uint1) = v.asLong

  implicit def uint8ToLong(v: uint8) = v.asLong

  implicit def uint16ToLong(v: uint16) = v.asLong

  implicit def uint32ToLong(v: uint32) = v.asLong

  implicit def int8ToLong(v: int8) = v.asLong

  implicit def int16ToLong(v: int16) = v.asLong

  implicit def int32ToLong(v: int32) = v.asLong

  implicit def floatToFloat(v: float) = v.asFloat

  implicit def doubleToFloat(v: double) = v.asFloat

  // From Scala to CTypes
  implicit def BooleanToUint1(v: Boolean): uint1 = uint1(v)

  // implicit def intToUInt(x: Int): UInt = UInt(x)
  // implicit def booleanToBool(x: Boolean): Bool = Bool(x)

  // These ones for the lazy programmer.
  // implicit def intToBoolean(x: Int): Boolean = if (x != 0) true else false
  // implicit def booleanToInt(x: Boolean): Int = if (x) 1 else 0

  // TODO test and add implicit conversions
}
