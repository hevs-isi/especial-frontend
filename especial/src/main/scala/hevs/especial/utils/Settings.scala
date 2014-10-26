package hevs.especial.utils

/**
 * Various settings for the project.
 */
object Settings {

  /**
   * Port used by the Scala TCP Monitor Server to communicate with the QEMU client.
   */
  final var MONITOR_TCP_CMD_PORT = 14001

  /**
   * Path to the root folder of QEMU for STM32. Relative to the Scala project path.
   */
  final var PATH_QEMU_STM32 = "./../../stm32/qemu_stm32"
}
