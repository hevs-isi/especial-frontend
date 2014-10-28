package hevs.especial.generator

import java.io.File
import java.nio.file.{Files, StandardCopyOption}

import hevs.especial.utils.{Context, OSUtils, Pipeline, Settings}

/**
 * Compile the generated (and formatted) C code for QEMU using an embedded toolchain for ARM.
 * All toolchain executable files must be linked to "/usr/bin" :
 * `arm-none-eabi-size`, `arm-none-eabi-gcc`, `arm-none-eabi-g++`, `arm-none-eabi-objcopy`
 */
class CodeCompiler extends Pipeline[String, String] {

  /**
   * Compile the generate code.
   *
   * @param ctx the context of the program with the logger
   * @param input the path of the generated C file
   * @return the path of the compiled binary (elf) file
   */
  def run(ctx: Context)(input: String): String = {
    // Must be on Linux for now...
    if (!OSUtils.isLinux) {
      ctx.log.error("Must be on Linux do compile the generated code !")
      return "" // Fatal error
    }

    // Copy the generated file to the C project to compile it along its Makefile
    val src = new File(input)
    val dst = new File(Settings.PROJECT_SRC_FILE)
    try {
      Files.copy(src.toPath, dst.toPath, StandardCopyOption.REPLACE_EXISTING)
      ctx.log.info(s"File copied to '$dst'.")
    }
    catch {
      case e: Exception =>
        ctx.log.error("Unable to copy the generated file.")
        return "" // Fatal error
    }

    // Ready to make
    // TODO: clean the project first ?
    ctx.log.info("Start compiling for QEMU...")
    val makeRes = OSUtils.runWithCodeResult("/usr/bin/make -r -j4 -C csrc/target-qemu/ all")
    if (makeRes._1 != 0) {
      ctx.log.error("Error when compiling the project !\n" + makeRes._2)
      return "" // Fatal error
    }

    // Return the elf file path
    val output = Settings.PROJECT_BINARY_FILE
    ctx.log.info(s"Binary file available in '$output'.")
    output
  }
}
