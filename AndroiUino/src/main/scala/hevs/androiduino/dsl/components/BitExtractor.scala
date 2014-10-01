package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals.{CType, Component, InputPort, OutputPort, hw_implemented, uint1}

import scala.reflect.runtime.universe._

@Deprecated
case class BitExtractor[T <: CType : TypeTag]() extends Component with hw_implemented {

  override val description = "A bit extractor"

  val in1 = new InputPort[T](this) {

    override val description = "All bits input"

    override def setInputValue(s: String): String = {
      // TODO: Here is the code for setting the first input
      s"input1 = $s" // TODO: tbd
    }
  }

  val out = new OutputPort[uint1](this) {

    override val description = "Extracted bit"

    override def getValue: String = {
      //"Here is the code for getting the output of the extractor
      s"// extracted bit code" // TODO
    }
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = Some(Seq(in1))
}
