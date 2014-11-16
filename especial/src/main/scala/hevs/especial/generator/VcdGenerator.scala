package hevs.especial.generator

import java.io.File
import java.util.Date

import hevs.especial.utils._

/**
 * Generate a VCD file to export values of outputs.
 *
 * VCD file format:
 * - http://en.wikipedia.org/wiki/Value_change_dump
 * - ftp://ece.buap.mx/pub/Secretaria_Academica/SDC/Active_HDL_4.2_Student_Version_Installer/Doc/avhdl/avh00229.htm
 */
class VcdGenerator extends Pipeline[Unit, Unit] {

  /** Output path of the generated code. */
  private final val OUTPUT_PATH = "output/%s/"

  def run(ctx: Context)(input: Unit): Unit = {

    if (!Settings.PIPELINE_RUN_VCDGEN) {
      ctx.log.info(s"$currentName is disabled.")
      return
    }

    // Create the folder if it not exist
    val path = String.format(OUTPUT_PATH, ctx.progName)
    val folder: RichFile = new File(path)
    if (!folder.createEmptyFolder())
      ctx.log.error(s"Unable to create the VCD file to '$path'.")

    // Create the VCD file
    val f: RichFile = new File(path + ctx.progName + ".vcd")

    val str = new StringBuilder
    str ++= generateHeader(ctx)
    str ++= addTimeScale(1)
    str ++= "$scope module logic $end\n"
    str ++= "$upscope $end\n$enddefinitions $end\n$dumpvars\n"
    str ++= "xid\n0id\n1id\nxid\n"

    f.write(str.result())

    ctx.log.info(s"VCD file generated to '$path'.")
  }

  private def generateHeader(ctx: Context): String = {
    // Add the date, the version and the file description
    """$date
      |   %s
      |$end
      |$version
      |   %s
      |$end
      |$comment
      |   VCD file generated automatically for '%s'.
      |$end
    """.stripMargin.format(new Date(), Version, ctx.progName)
  }

  private def addTimeScale(ms: Int): String = "$timescale %dms $end\n".format(ms)

  private def addDigitalOutput(id: Int, name: String): String = {
    // $var type bitwidth id name
    val wire = "$var wire 8 %d %s $end"
    wire.format(id, name)
  }

  private def generateCode(ctx: Context): Unit = {

  }
}

/*

File OK

$date
   Sun Nov 16 16:42:27 CET 2014
$end
$version
   ESPecIaL version a1.1
$end
$comment
   VCD file generated automatically for 'vcdTest'.
$end
$timescale 10ms $end
$scope module logic $end
$var wire 1 1 id $end
$upscope $end
$enddefinitions $end
$dumpvars
11
$end
#0
01
#1
11
#2
x1
#3
01
#4
11

 */