package hevs.especial.utils

/**
 * Global settings for the project, especially to modify the [[Pipeline]] behaviour.
 *
 * Enable or disable [[Pipeline]] blocks, set external tools paths, etc.
 */
object Settings {

  /* DOT */

  /** Generate the DOT diagram or PDF file corresponding to the program. */
  val PIPELINE_RUN_DOT: Boolean = true

  /** Convert the dot source to PDF. */
  val PIPELINE_EXPORT_PDF: Boolean = true // PIPELINE_RUN_DOT must be enabled and DOT installed


  /* CHECKER */

  /** Print warnings before generating the code from the DSL program. */
  val PIPELINE_RUN_CODE_CHECKER: Boolean = true


  /* OPTIMIZER */

  /** Optimize the code and remove all unused components. */
  val PIPELINE_RUN_CODE_OPTIMIZER: Boolean = true


  /* RESOLVER */

  /** Define the maximum number of passes. After that, the resolver will stop. */
  val RESOLVER_MAX_PASSES: Int = 64


  /* CODE GENERATOR */

  /** Generate a verbose C/C++ code with comments and section names or not. */
  val GEN_VERBOSE_CODE: Boolean = true


  /* CODE FORMATTER */

  /**
   * Format the generated C code using AStyle.
   * The original file is automatically renamed with a ".orig" extension.
   */
  val PIPELINE_RUN_FORMATTER: Boolean = true

  /** Path to the AStyle binary file */
  val ASTYLE_PATH = "./third_party/astyle/%s"


  /* CODE COMPILER */

  /** Copy and compile the generated C code. */
  val PIPELINE_RUN_COMPILER: Boolean = true

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
