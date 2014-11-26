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
abstract class Mux[T <: CType : TypeTag](nbrIn: Int) extends GenericCmp[T, T](nbrIn, 1) with HwImplemented with Out1 {

  /* I/O management */

  private val selValName = valName("sel") // Global variable used to store the selection pin value

  // Input n°3 is the selection pin.
  // Always a `uint8` input
  val sel = new InputPort[uint8](this) {
    override val name = "sel"
    override val description = "the selection input"

    // Connection an output to the sel input
    // Will print "sel_cmp6 = 2;"
    // FIXME: check if the index exist ? Add default case ?
    override def setInputValue(s: String) = s"$selValName = $s"
  }
  addCustomIn(sel)

  // Single output connected here
  override val out: OutputPort[T] = out(0)


  override def setInputValue(index: Int, s: String) = s"${inValName(index)} = $s"

  override def getOutputValue(index: Int) = {
    assert(index == 0) // Only one output

    // Simple if/else if they are only two inputs. The else is the "default" branch.
    if(nbrIn == 2) {
       s"""
          |// ${Mux.this}
          |if($selValName == 0)
          |  ${outValName()} = ${inValName(0)};
          |else
          |  ${outValName()} = ${inValName(1)};""".stripMargin
    }
    else {
      // Create all cases statements in the switch/case
      val cases = for(i <- 0 until nbrIn) yield addCaseStatement(i)

      // Store the Mux result in a temporary variable
      s"""
        |// ${Mux.this}
        |${bool().getType} ${outValName()};
        |switch($selValName) {
        |${cases.mkString("\n")}
        |}""".stripMargin
    }
  }

  private def addCaseStatement(index: Int) = {
    s"""case $index:
        |  ${outValName()} = ${inValName(index)};
        |  break;"""
  }

  /* Code generation */

  override def getGlobalCode = {
      // Define inputs, output and sel as global variables
      val varIn = for(i <- 0 until nbrIn) yield inValName(i)
      val vars = List(selValName, outValName()) ++ varIn

      // Print all boolean variables to declare from the list
      Some(s"${bool().getType} ${vars.mkString(", ")}; // $this")
  }

  override def getLoopableCode = {
    val result: ListBuffer[String] = ListBuffer()

    // Mux code
    result += s"${out.getValue}"

    // Set the output value to connected components
    for (inPort ← ComponentManager.findConnections(out))
      result += inPort.setInputValue(s"${outValName()}") + "; // " + inPort

    Some(result.mkString("\n"))
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