package hevs.especial.simulation

/**
 * Code compilation, simulation and VCD export using QEMU.
 *
 * The [[Sch5SimCode]] is simulated in QEMU. After 6 loop ticks, output values are exported in a VCD file.
 * Output values are the following :
 * {{
 * Pin 'C#03' has 06 values:	1-0-1-0-1-0
 * Pin 'C#04' has 06 values:	0-1-0-1-0-1
 * }}
 *
 * @version 1.0
 * @author Christopher Metrailler (mei@hevs.ch)
 */
class Sch5Simulation extends MonitorTest {

  /** DSL program under test. */
  runTests(new Sch5SimCode())
}
