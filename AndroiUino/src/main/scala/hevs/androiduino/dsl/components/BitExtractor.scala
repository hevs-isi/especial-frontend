package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals.{CType, Component, InputPort, OutputPort, hw_implemented, uint1}

case class BitExtractor[T <: CType](inputType: T) extends Component("a bit extractor") with hw_implemented {
  val in1 = new InputPort(inputType, this, Some("All bits input")) {
    override def updateValue(s: String): String = {
      // TODO: Here is the code for setting the first input
      s"input1 = $s" // TODO: tbd
    }
  }
  val out = new OutputPort(uint1(), this, Some("Extracted bit")) {
    override def getValue(): String = {
      //"Here is the code for getting the output of the extractor
      s"// extracted bit code" // TODO
    }
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = Some(Seq(in1))
}