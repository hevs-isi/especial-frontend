package hevs.especial.dsl.components

/**
 * A component without any input or output.
 * Used to modify the generated C code.
 */
trait NoIO extends Component {
  // Override input and output definitions, so the code of the component is minimal.
  override def getOutputs = None
  override def getInputs = None
}

/* Inputs */

trait In1 {
  val in: InputPort[_]
}

trait In2 {
  protected type I = InputPort[_]
  val in1: I
  val in2: I
}

trait In3 extends In2 {
  val in3: I
}

trait In4 extends In3 {
  val in4: I
}

/* Outputs */

trait Out1 {
  val out: OutputPort[_]
}

trait Out2 {
  protected type O = OutputPort[_]
  val out1: O
  val out2: O
}

trait Out3 extends Out2 {
  val out3: O
}

trait Out4 extends Out3 {
  val out4: O
}