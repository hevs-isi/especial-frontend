package hevs.especial.utils

/**
 * Various settings for the project, especially to modify the pipeline behaviour.
 * Enable or disable options for the compiler path. Set paths for external tools.
 */
object Settings {

  /* DOT */

  /** Generate the DOT diagram or PDF file corresponding to the program. */
  val PIPELINE_RUN_DOT: Boolean = true

  val PIPELINE_EXPORT_PDF: Boolean = true // PIPELINE_RUN_DOT must be enabled and DOT installed



  /* OPTIMIZER */

  /**
   * Print warnings before generating the code from the DSL program.
   * Optimize the code and remove all unused components.
   */
  val PIPELINE_RUN_CODE_OPTIMIZER: Boolean = true



  /* RESOLVER */

  /** Define the maximum number of passes. After that, the resolver will stop. */
  val RESOLVER_MAX_PASSES: Int = 64


  /* CODE FORMATTER */

  /**
   * Format the generated C code using AStyle.
   * The original file is automatically renamed with a ".orig" extension.
   */
  val PIPELINE_RUN_FORMATTER: Boolean = true

  /** Path to the AStyle binary file */
  val ASTYLE_PATH = "./third_party/astyle/%s"


  /* CODE COMPILER */

  /** Path of the source file in the C project */
  val PROJECT_SRC_FILE = "csrc/src/main.cpp"

  /** Path of the binary (elf) file */
  val PROJECT_BINARY_FILE = "csrc/target-qemu/csrc.elf"


  /* QEMU */

  /** Path to the root folder of QEMU for STM32. Relative to the Scala project path. */
  val PATH_QEMU_STM32 = "../../stm32/qemu_stm32"


  /* MONITOR */

  /** Port used by the Scala TCP Monitor Server to communicate with the QEMU client. */
  val MONITOR_TCP_CMD_PORT = 14001
  val MONITOR_TCP_EVT_PORT = 14002

  /** Enable events with acknowledge or not */
  val MONITOR_ACK_EVENTS = true


  /* VCD generator */

  /** Generate the VCD file to plot the waveform of program outputs. */
  val PIPELINE_RUN_VCDGEN: Boolean = true
}
