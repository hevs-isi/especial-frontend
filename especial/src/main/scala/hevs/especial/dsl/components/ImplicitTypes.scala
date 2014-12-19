package hevs.especial.dsl.components

import scala.language.implicitConversions

/**
 * Implicit conversions for CType <-> Scala.
 */
object ImplicitTypes {

  // From CTypes to Scala
//  implicit def boolToLong(v: bool) = v.asLong
//  implicit def uint8ToLong(v: uint8) = v.asLong
//  implicit def uint16ToLong(v: uint16) = v.asLong
//  implicit def uint32ToLong(v: uint32) = v.asLong
//  implicit def int8ToLong(v: int8) = v.asLong
//  implicit def int16ToLong(v: int16) = v.asLong
//  implicit def int32ToLong(v: int32) = v.asLong
//  implicit def floatToFloat(v: float) = v.asFloat
//  implicit def doubleToFloat(v: double) = v.asFloat

  // Create `CTypes` from `Scala`

  implicit def BooleanToBool(v: Boolean): bool = bool(v)

  implicit def ByteToInt8(v: Byte): int8 = int8(v)

  implicit def ShortToInt16(v: Short): int16 = int16(v)
  implicit def ShortToUint8(v: Short): uint8 = uint8(v)

  implicit def IntToInt32(v: Int): int32 = int32(v)
  implicit def IntToUint16(v: Int): uint16 = uint16(v)

  implicit def LongToUint32(v: Long): uint32 = uint32(v)

  implicit def FloatToFloat(v: Float): float = float(v)

  implicit def DoubleToDouble(v: Double): double = double(v)


  // implicit def intToUInt(x: Int): UInt = UInt(x)
  // implicit def booleanToBool(x: Boolean): Bool = Bool(x)

  // These ones for the lazy programmer.
  // implicit def intToBoolean(x: Int): Boolean = if (x != 0) true else false
  // implicit def booleanToInt(x: Boolean): Int = if (x) 1 else 0

  // TODO test and add implicit conversions


  // Conversions inside CType

  implicit def BoolToUint8(v: bool): uint8 = if (v.asBool) uint8(1) else uint8(0)
}
