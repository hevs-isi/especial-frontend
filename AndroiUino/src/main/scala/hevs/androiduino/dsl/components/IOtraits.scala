package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals.{InputPort, OutputPort}

trait In1 {
  def in: InputPort[_]
}

trait In2 {
  type I = InputPort[_]

  def in1: I

  def in2: I
}

trait In3 extends In2 {
  def in3: I
}

trait In4 extends In3 {
  def in4: I
}


trait Out1 {
  def out: OutputPort[_]
}

trait Out2 {
  type O = OutputPort[_]

  def out1: O

  def out2: O
}

trait Out3 extends Out2 {
  def out3: O
}

trait Out4 extends Out3 {
  def out4: O
}