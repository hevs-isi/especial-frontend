package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals._

// FIXME remove this, use only one
private object IdGenerator {
  private var id = 0

  def getUniqueID: Int = {
    id = id + 1
    id
  }
}

case class Constant(t: CType) extends Component("a constant generator") with hw_implemented {
  // autoCst + id composant + id unique
  val valName = s"cstComp${id}_${IdGenerator.getUniqueID}"

  val out = new OutputPort(t, this) {
    override def getValue: String = valName
  }

  def getOutputs = Some(Seq(out))

  def getInputs = None


  /**
   * Constant declaration in the C code. Will print something like `const bool_t _autoCst1 = false;`.
   * @return the constant declaration as boolean
   */
  override def getGlobalConstants = Some(s"const ${t.getType} $valName = ${t.asBool};")

  override def getBeginOfMainAfterInit = {
    var result = "// Propagating constants\n"

    for (wire ← out.wires)
      result += wire.b.updateValue(s"$valName") + ";\n"

    Some(result)
  }
}