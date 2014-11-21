package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Helper class used to create a `Mux` with a generic number of inputs.
 * All I/O have the same type. Output and selection pin are added manually.
 *
 * @param nbrIn number of generic inputs for the component (without the selection pin)
 * @tparam T I/O type of the component
 */
abstract class Mux[T <: CType : TypeTag](nbrIn: Int) extends GenericCmp[T, T](nbrIn, 0) with HwImplemented {

  // FIXME: not all types are compatible for now. Switch case is used...
  assert(typeOf[T] == typeOf[bool])

  private val selValName = valName("sel") // Global variable used to store the selection pin value

  // Input n°3 is the selection pin. The type of this input is the same type as other inputs.
  val sel = new InputPort[T](this) {
    override val name = "sel"
    override val description = "the selection input"

    // Connection an output to the sel input
    override def setInputValue(s: String) = s"$selValName = $s"
  }
  addCustomIn(sel)

  val out = new OutputPort[T](this) {
    override val name = s"out"
    override val description = "the selection input"

    // FIXME: not generic
    // FIXME: check if the input is a boolean or an int
    override def getValue = {
      s"""
        |// ${Mux.this}
        |${bool().getType} ${outValName(0)};
        | switch($selValName) {
        |  case 0:
        |    ${outValName(0)} = ${inValName(0)};
        |    break;
        |
        |  case 1:
        |    ${outValName(0)} = ${inValName(1)};
        |    break;
        |}
      """.stripMargin
    }
  }
  addCustomOut(out)

  override def setInputValue(index: Int, s: String) = s"${inValName(index)} = $s"

  override def getOutputValue(index: Int) = null // No generic output used


  /* Code generation */

  // Global variables
  override def getGlobalCode = Some(s"${bool().getType} $selValName, ${inValName(0)}, ${inValName(1)}; // $this")

  override def getLoopableCode = out.isConnected match {
    case true =>
      val result: ListBuffer[String] = ListBuffer()

      // Mux code
      result += s"${out.getValue}"

      // Set the output value to connected components
      for (inPort ← ComponentManager.findConnections(out))
        result += inPort.setInputValue(s"${outValName(0)}") + "; // " + inPort

      Some(result.mkString("\n"))
    case _ => None
  }
}


case class Mux2[T <: CType : TypeTag]() extends Mux[T](2) with In3 with Out1 {
  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = sel // `in3` is an alias for `sel`
}
