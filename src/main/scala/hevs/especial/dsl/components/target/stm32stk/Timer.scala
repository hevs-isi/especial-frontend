package hevs.especial.dsl.components.target.stm32stk

import hevs.especial.dsl.components._
import hevs.especial.utils.Settings

import scala.concurrent.duration.Duration

/**
 * Base class of the [[PeriodicTimer]] and [[SingleShotTimer]] class.
 *
 * @version 1.0
 * @author Christopher Métrailler (mei@hevs.ch)
 */
abstract class Timer extends Component with Out1 with HwImplemented {

  // TODO: add an enable or reset input
  // FIXME: the timer period should be an input of the block

  /* I/O management */

  /**
   * The [[bool]] output which indicate when the timer overflows.
   * The output value is high during one cycle only.
   */
  override val out = new OutputPort[bool](this) {
    override val name = s"out"
    override val description = "timer overflow"

    // varName contains the output value
    override def getValue: String = s"$outName"
  }

  override def getOutputs = Some(Seq(out))

  override def getInputs = None


  /* Code generation */

  protected val timerName: String = "timer" + f"$cmpId%02d"

  protected val outName = s"out_$timerName"

  // FIXME: this should be called only once event if multiple timer are used in the same program
  override def getInitCode = Some("time_init();")

  override def getIncludeCode = Seq("utils/time.h")
}

/**
 * Implementation of a repetitive periodic timer.
 *
 * @param delay the initial delay of the timer or `0` if not used
 * @param period the repetitive timer period
 *
 * @version 1.0
 * @author Christopher Métrailler (mei@hevs.ch)
 */
class PeriodicTimer private[stm32stk](delay: Duration, period: Duration) extends Timer {

  assert(delay.toMillis >= 0, "Invalid timer delay, cannot be negative !")
  assert(period.toMillis >= 0, "Invalid timer period, cannot be negative !")

  /* I/O management */

  override val description = s"periodic timer\\n ${period.toString}" // Pretty print of the period


  /* Code generation */

  override def getBeginOfMainAfterInit = Some(s"$timerName = time_set_timeout_ms(${delay.toMillis});")

  // Define the timer structure and its output value
  override def getGlobalCode = {
    val time = s"timeout_t $timerName;"
    val overflow = s"${bool().getType} $outName;"
    val res = time + "\n" + overflow
    if (Settings.GEN_VERBOSE_CODE)
      Some(res + "\t" * 5 + s"// $out") // Print a description of the output
    else
      Some(res)
  }

  // Check the timer overflow and update the timer period if necessary
  override def getLoopableCode = {
    val res = new StringBuilder
    res ++= s"$outName = (time_diff_ms($timerName, time_get()) <= 0);"
    res ++= s"if($outName)\n"
    res ++= s"$timerName = time_set_timeout_ms(${period.toMillis});"
    Some(res.result())
  }
}

/**
 * Implementation of a single-shot timer, executed only once.
 *
 * @param period the timer period (executed once)
 *
 * @version 1.0
 * @author Christopher Métrailler (mei@hevs.ch)
 */
class SingleShotTimer private[stm32stk](period: Duration) extends PeriodicTimer(period, Duration.Zero) {

  /* I/O management */

  override val description = s"single-shot timer\\n ${period.toString}" // Pretty print of the period


  /* Code generation */

  private val executed = s"end_$timerName"

  // Boolean flag added to check if the timer has been executed or not
  override def getGlobalCode = Some(super.getGlobalCode.get + s"\n ${bool().getType} $executed = false;")

  // Check the timer overflow and check to execute it only once
  override def getLoopableCode = {
    val res = new StringBuilder
    res ++= s"$outName = (!$executed && time_diff_ms($timerName, time_get()) <= 0);"
    res ++= s"if($outName)\n" // Check overflow
    res ++= s"$executed = true;" // Single-shot timer executed
    Some(res.result())
  }

}


object Timer {

  /**
   * Create a periodic timer.
   *
   * @param delay the initial delay of the timer or `0` if not used
   * @param period the repetitive timer period
   * @return the periodic timer
   */
  def apply(period: Duration, delay: Duration = Duration.Zero) = new PeriodicTimer(delay, period)
}

object DelayedEvent {

  /**
   * Create a single-shot timer.
   * @param period the timer period (executed once)
   * @return the single-shot timer
   */
  def apply(period: Duration) = new SingleShotTimer(period)
}