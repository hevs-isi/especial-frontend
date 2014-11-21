package hevs.especial.dsl.components

/**
 * Abstract type that can be used in the native C code.
 *
 * A `CType` can be converted to a Scala type using `asBool`,
 * `asString`, `asLong`, `asFloat` and `asDouble` methods.
 * A `java.io.IOException` is thrown if the conversion is not implemented.
 *
 * @author Christopher Métrailler (christopher.metrailler@epfl.ch)
 */
sealed abstract class CType(val v: Any) {

  /**
   * @return a `scala.Long` value
   */
  def asLong: Long = sys.error("Conversion not available.")

  /**
   * @return a `scala.String` value
   */
  def asString: String = sys.error("Conversion not available.")

  /**
   * @return a `scala.Boolean` value
   */
  def asBool: Boolean = sys.error("Conversion not available.")

  /**
   * @return a `scala.Float` value
   */
  def asFloat: Float = sys.error("Conversion not available.")

  /**
   * @return a `scala.Double` value
   */
  def asDouble: Double = sys.error("Conversion not available.")

  /**
   * The string name of the type (according to `<stdint.h>`)
   * @return the C type as a `String` to add in the generated code
   */
  def getType: String

  override def toString = s"$getType:$v"
}

/**
 * A boolean value, equivalent to `scala.Boolean`.
 * Can be converted to a `scala.Int` or `scala.Boolean`.
 *
 * @param v the value
 */
case class bool(override val v: Boolean = false) extends CType(v) {
  override def getType = "bool"

  override def asBool = v

  override def asLong = if (v) 1 else 0
}


/**
 * An unsigned 8-bit integer.
 * Can be converted to a `scala.Long`.
 *
 * @param v the value, from `0x00` to `0xFF`
 */
case class uint8(override val v: Short = 0) extends CType(v) {
  require(v >= 0x00, "Unsigned number, must be positive.")
  require(v <= 0xFF, "8-bit number max.")

  override def getType = "uint8_t"

  override def asLong = v.toLong
}

/**
 * An unsigned 16-bit integer.
 * Can be converted to a `scala.Long`.
 *
 * @param v the value, from `0x00` to `0xFFFF`
 */
case class uint16(override val v: Int = 0) extends CType(v) {
  require(v >= 0x0000, "Unsigned number, must be positive.")
  require(v <= 0xFFFF, "16-bit number max.")

  override def getType = "uint16_t"

  override def asLong = v.toLong
}

/**
 * An unsigned 32-bit integer.
 * Can be converted to a `scala.Long`.
 *
 * @param v the value, from `0x00` to `0xFFFFFFFF`
 */
case class uint32(override val v: Long = 0) extends CType(v) {
  require(v >= 0x00000000, "Unsigned number, must be positive.")
  require(v <= 0xFFFFFFFF, "32-bit number max.")

  override def getType = "uint32_t"

  override def asLong = v.toLong
}

/**
 * A signed 8-bit integer, equivalent to `scala.Byte`.
 * Can be converted to a `scala.Long`.
 *
 * @param v the value
 */
case class int8(override val v: Byte = 0) extends CType(v) {
  override def getType = "int8_t"

  override def asLong = v.toLong
}

/**
 * A signed 16-bit integer, equivalent to `scala.Short`.
 * Can be converted to a `scala.Long`.
 *
 * @param v the value
 */
case class int16(override val v: Short = 0) extends CType(v) {
  override def getType = "int16_t"

  override def asLong = v.toLong
}

/**
 * A signed 32-bit integer, equivalent to `scala.Int`.
 * Can be converted to a `scala.Long`.
 *
 * @param v the value
 */
case class int32(override val v: Int = 0) extends CType(v) {
  override def getType = "int32_t"

  override def asLong = v.toLong
}

/**
 * A 32-bit IEEE-754 floating point number, equivalent to `scala.Float`.
 * Can be converted to a `scala.Long`.
 *
 * @param v the value
 */
case class float(override val v: Float = 0) extends CType(v) {
  override def getType = "float"

  override def asFloat = v

  override def asLong = v.toLong

  override def asDouble = v.toDouble
}

/**
 * A 64-bit IEEE-754 floating point number, equivalent to `scala.Double`.
 * Can be converted to a `scala.Long`.
 *
 * @param v the value
 */
case class double(override val v: Double = 0) extends CType(v) {
  override def getType = "double"

  override def asDouble = v

  override def asLong = v.toLong

  override def asFloat = v.toFloat
}