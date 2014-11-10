package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components.{Component, NoIO, hw_implemented}

/**
 * STM32-103STK board
 * https://www.olimex.com/Products/ARM/ST/STM32-103STK/
 */
class Stm32stk extends Component with hw_implemented with NoIO {

  override val description = "STM32-103STK board"

  override def getIncludeCode: Option[String] = Some("helper.h")
    //  """extern "C" {
    //  |  #include "helper.h"
    //  |}""".stripMargin)
}
