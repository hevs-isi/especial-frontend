package hevs.androiduino.dsl.components.fundamentals

// Bake with spire ? https://github.com/non/spire
// require(x > 0 && < x < 255)

// Not using inheritance but traits because it's not 
// a good idea with case classes 
// (see http://stackoverflow.com/questions/12289806/scala-extending-parameterized-abstract-class)
// To make usable in case classes
class C_Types(val v: AnyVal) {}

case class uint1(override val v: Boolean = false) extends C_Types(v)
{
	override def toString = "bool"
}

case class uint8(override val v: Byte = 0) extends C_Types(v)
{
	override def toString = "uint8_t"
}

case class uint16(override val v: Short = 0) extends C_Types(v)
{
	override def toString = "uint16_t"
}

case class uint32(override val v: Int = 0) extends C_Types(v)
{
	override def toString = "uint32_t"
}

case class int8(override val v: Byte = 0) extends C_Types(v)
{
	override def toString = "int8_t"
}

case class int16(override val v: Short = 0) extends C_Types(v)
{
	override def toString = "int16_t"
}

case class int32(override val v: Int = 0) extends C_Types(v)
{
	override def toString = "int32_t"
}

case class float(override val v: Float = 0) extends C_Types(v)
{
	override def toString = "float"
}

case class double(override val v: Double = 0) extends C_Types(v)
{
	override def toString = "double"
}
