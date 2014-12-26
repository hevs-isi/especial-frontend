package hevs.especial.dsl.components.core

import hevs.especial.dsl.components._

import scala.Boolean
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._

/**
 * Helper class used to create a `Mux` with a generic number of inputs.
 *
 * Inputs and outputs have the same type. The selection pin is always an [[uint8]] port.
 *
 * @version 2.0
 * @author Christopher Metrailler (mei@hevs.ch)
 *
 * @param nbrIn number of generic inputs for the component (without the selection pin)
 * @tparam T I/O type of the component
 */
abstract class Mux[T <: CType : TypeTag](nbrIn: Int) extends GenericCmp[T, T](nbrIn, 1) with HwImplemented with Out1 {

  override val description = s"Mux$nbrIn"

  /* I/O management */

  private val selValName = valName("sel") // Global variable used to store the selection pin value

  // The last input is the selection pin.
  // Always a `uint8` input
  val sel = new InputPort[uint8](this) {
    override val name = "sel"
    override val description = "the selection input"
  }
  addCustomIn(sel)

  // Single output connected here
  override val out: OutputPort[T] = out(0)

  /**
   * Result of the if/switch case of the Mux.
   */
  override def getOutputValue(index: Int) = {
    assert(index == 0) // Index not used, only one output
    s"${outValName()}"
  }


  /* Code generation */

  override def getLoopableCode = {
    val result = ListBuffer.empty[String]

    // Local variable to store the selection and output values
    val selValue = ComponentManager.findPredecessorOutputPort(sel).getValue
    result += s"${getTypeString[uint8]} $selValName = $selValue;"

    // Simple if/else if they are only two inputs. The else is the "default" branch.
    if (nbrIn == 2) {
      result += s"${getTypeString[T]} ${outValName()};"
      result += s"""
          |if($selValName == 0)
          |  ${outValName()} = ${readInput(0)};
          |else
          |  ${outValName()} = ${readInput(1)};""".stripMargin
      Some(result.mkString("\n"))
    }

    // Switch/case used if more than two inputs
    else {
      // Create all cases statements
      val cases = for (i <- 0 until nbrIn) yield addCaseStatement(i)

      // Store the Mux result in a temporary variable
      result += s"${getTypeString[T]} ${outValName()} = 0;"
      result += s"""
        |switch($selValName) {
        |  ${cases.mkString("\n")}
        |}""".stripMargin
    }

    Some(result.mkString("\n"))
  }

  private def addCaseStatement(index: Int) = {
    s"""case $index:
        |  ${outValName()} = ${readInput(index)};
        |  break;"""
  }

  private def readInput(index: Int = 0) = {
    ComponentManager.findPredecessorOutputPort(in(index)).getValue
  }
}


/* Predefined Mux components */

case class Mux2[T <: CType : TypeTag]() extends Mux[T](2) with In2 {
  override val in1 = in(0)
  override val in2 = in(1)
}

case class Mux3[T <: CType : TypeTag]() extends Mux[T](3) with In3 {
  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
}

case class Mux4[T <: CType : TypeTag]() extends Mux[T](4) with In4 {
  override val in1 = in(0)
  override val in2 = in(1)
  override val in3 = in(2)
  override val in4 = in(3)
}