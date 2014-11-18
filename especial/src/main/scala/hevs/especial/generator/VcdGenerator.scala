package hevs.especial.generator

import java.io.File
import java.util.Date

import hevs.especial.dsl.components.Pin
import hevs.especial.utils._

import scala.collection.mutable

//TODO: add the events as a bus. How to synchronize the event bus with I/O ?

/**
 * Generate a VCD file to export values of outputs.
 *
 * VCD file format:
 * - http://en.wikipedia.org/wiki/Value_change_dump
 * - ftp://ece.buap.mx/pub/Secretaria_Academica/SDC/Active_HDL_4.2_Student_Version_Installer/Doc/avhdl/avh00229.htm
 */
class VcdGenerator extends Pipeline[Map[Pin, Seq[Int]], Unit] {

  /** Output path of the generated code. */
  private final val OUTPUT_PATH = "output/%s/"

  def run(ctx: Context)(input: Map[Pin, Seq[Int]]): Unit = {

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
    val fileName = path + ctx.progName + ".vcd"
    val f: RichFile = new File(fileName)

    val str = new StringBuilder
    str ++= startHeader(ctx)
    str ++= setTimeScale(1)
    str ++= addScope(ctx, input.keySet)
    str ++= endHeader()

    str ++= setDumpVars(input.keySet)
    str ++= setValueChange(input)

    f.write(str.result()) match {
      case true => ctx.log.info(s"VCD file generated to '$fileName'.")
      case _ => ctx.log.error(s"Unable to generate the VCD file to '$fileName'.")
    }
  }

  private def startHeader(ctx: Context): String = {
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

  private def setTimeScale(ms: Int) = "\n$timescale %d ms $end\n\n".format(ms)

  private def addScope(ctx: Context, pins: Set[Pin]): String = {
    val res = new StringBuilder
    res ++= "$scope module %s $end\n".format(ctx.progName) // The module name is the program name

    // Declare all available pins as variable in the current scope
    val vars = for (p <- pins) yield addVarToScope(p)
    res ++= vars.mkString("\n")

    res ++= "\n$upscope $end\n"
    res.result()
  }

  // Add a variable in a scope
  private def addVarToScope(pin: Pin, bitwidth: Int = 1): String = {
    // The format is `$var type bitwidth id name`.
    // ID and name cannot contain space or any special character.
    val wire = "$var wire %d %s %s $end"
    wire.format(bitwidth, pin.getIdentifier, "pin_" + pin.getIdentifier)
  }

  private def endHeader() = "\n$enddefinitions $end\n\n"

  // Set initial values of all variables dumped
  private def setDumpVars(initValues: Set[Pin]) = {
    val res = new StringBuilder
    res ++= "$dumpvars\n"

    // Set the initial value of the pin
    val vals = for (p <- initValues) yield s"x${p.getIdentifier}"
    res ++= vals.mkString("\n")

    res ++= "\n$end\n\n"
    res.result()
  }

  // Value change section
  private def setValueChange(values: Map[Pin, Seq[Int]]) = {
    // Overrides values of the `$dumpvars` section.
    // Set values from time #0.
    val dump = mutable.Map.empty[Int, Seq[String]]

    // FIXME: optimization ?
    // FIXME: do not write the pin value is it has not changed
    for ((pin, values) <- values) {
      for ((v, time) <- values.zipWithIndex) {
        // Value of the pin (or old new, no optimizations)
        val pinVal = s"$v${pin.getIdentifier}"

        dump.get(time) match {
          case Some(current) =>
            // Append to new value at the end of the list
            dump.update(time, current :+ pinVal)
          case None =>
            // Add the first value for this timestamp
            dump += time -> Seq(pinVal)
        }
      }
    }

    // Final file content.
    // Timestamps must be ordered ascending.
    val res = new StringBuilder
    for ((time, values) <- dump.toSeq.sortBy(_._1)) {
      res ++= s"#$time\n" // Add the timestamp value
      res ++= values.mkString("\n")
      res ++= "\n\n"
    }
    res.result()
  }
}