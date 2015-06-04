package hevs.especial.dsl.components

/**
 * Implicit conversions for [[CType]] <-> Scala.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
object CType {

  //TODO: add a new 'Event' type (based on a boolean value)

  /**
   * Map the [[CType]] class with the corresponding C type (according to `<stdint.h>`).
   */
  val t = Map[Class[_], String](bool().getClass -> "bool", uint8().getClass -> "uint8_t",
    uint16().getClass -> "uint16_t", uint32().getClass -> "uint32_t", int8().getClass -> "int8_t",
    int16().getClass -> "int16_t", int32().getClass -> "int32_t", float().getClass -> "float",
    double().getClass -> "double")

  import scala.language.implicitConversions

  object Implicits {
    // Conversion from Scala to CType

    implicit def BooleanToBool(v: Boolean): bool = bool(v)

    implicit def ByteToInt8(v: Byte): int8 = int8(v)

    implicit def ShortToInt16(v: Short): int16 = int16(v)

    implicit def ShortToUint8(v: Short): uint8 = uint8(v)

    implicit def IntToInt32(v: Int): int32 = int32(v)

    implicit def IntToUint16(v: Int): uint16 = uint16(v)

    implicit def LongToUint32(v: Long): uint32 = uint32(v)

    implicit def FloatToFloat(v: Float): float = float(v)

    implicit def DoubleToDouble(v: Double): double = double(v)


    // Conversions inside CType

    implicit def BoolToUint8(v: bool): uint8 = if (v.asBool) uint8(1) else uint8(0)
  }

}

/**
 * Abstract type that can be used in the native C code.
 *
 * A [[CType]] can be converted to a Scala type using `asBool`,
 * `asString`, `asLong`, `asFloat` and `asDouble` methods.
 * A [[java.io.IOException]] is thrown if the conversion is not available.
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
sealed abstract class CType(val v: Any) {

  /** @return a [[Long]] value */
  def asLong: Long = sys.error("Conversion not available.")

  /** @return a [[String]] value */
  def asString: String = sys.error("Conversion not available.")

  /** @return a [[Boolean]] value */
  def asBool: Boolean = sys.error("Conversion not available.")

  /** @return a [[Float]] value */
  def asFloat: Float = sys.error("Conversion not available.")

  /** @return a [[Double]] value */
  def asDouble: Double = sys.error("Conversion not available.")

  /**
   * The string name of the type (according to `<stdint.h>`), used in the generated code.
   * @return the C type as [[String]] used in the generated code
   */
  final def getType: String = CType.t.get(this.getClass).get

  /**
   * @return the raw value displayed as [[String]]
   */
  final def getValue: String = String.valueOf(v)


  override def toString = s"$getType:$v"
}

/**
 * A boolean value, equivalent to [[Boolean]].
 * Can be converted to [[Int]] or [[Boolean]].
 *
 * @param v the value
 */
case class bool(override val v: Boolean = false) extends CType(v) {
  override def asBool = v

  override def asLong = if (v) 1 else 0
}

/**
 * An unsigned 8-bit integer.
 * Can be converted to [[Long]].
 *
 * @param v the value, from `0x00` to `0xFF`
 */
case class uint8(override val v: Short = 0) extends CType(v) {
  require(v >= 0x00, "Unsigned number, must be positive.")
  require(v <= 0xFF, "8-bit number max.")

  override def asLong = v.toLong
}

/**
 * An unsigned 16-bit integer.
 * Can be converted to [[Long]].
 *
 * @param v the value, from `0x00` to `0xFFFF`
 */
case class uint16(override val v: Int = 0) extends CType(v) {
  require(v >= 0x0000, "Unsigned number, must be positive.")
  require(v <= 0xFFFF, "16-bit number max.")

  override def asLong = v.toLong
}

/**
 * An unsigned 32-bit integer.
 * Can be converted to [[Long]].
 *
 * @param v the value, from `0x00` to `0xFFFFFFFF`
 */
case class uint32(override val v: Long = 0) extends CType(v) {
  require(v >= 0x00000000, "Unsigned number, must be positive.")

  override def asLong = v.toLong
}

/**
 * A signed 8-bit integer, equivalent to `scala.Byte`.
 * Can be converted to [[Long]].
 *
 * @param v the value
 */
case class int8(override val v: Byte = 0) extends CType(v) {
  override def asLong = v.toLong
}

/**
 * A signed 16-bit integer, equivalent to `scala.Short`.
 * Can be converted to [[Long]].
 *
 * @param v the value
 */
case class int16(override val v: Short = 0) extends CType(v) {
  override def asLong = v.toLong
}

/**
 * A signed 32-bit integer, equivalent to `scala.Int`.
 * Can be converted to [[Long]].
 *
 * @param v the value
 */
case class int32(override val v: Int = 0) extends CType(v) {
  override def asLong = v.toLong
}

/**
 * A 32-bit IEEE-754 floating point number, equivalent to [[Float]].
 * Can be converted to [[Long]] or [[Double]].
 *
 * @param v the value
 */
case class float(override val v: Float = 0) extends CType(v) {
  override def asFloat = v

  override def asLong = v.toLong

  override def asDouble = v.toDouble
}

/**
 * A 64-bit IEEE-754 floating point number, equivalent to [[Double]].
 * Can be converted to [[Long]] or [[Float]].
 *
 * @param v the value
 */
case class double(override val v: Double = 0) extends CType(v) {
  override def asDouble = v

  override def asLong = v.toLong

  override def asFloat = v.toFloat
}