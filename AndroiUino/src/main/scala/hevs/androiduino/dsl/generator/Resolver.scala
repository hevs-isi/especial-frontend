package hevs.androiduino.dsl.generator

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.fundamentals.{Component, hw_implemented}

import scala.collection.mutable

// Unconnected components are ignored !!
class Resolver extends Logging {

  // IDs of components that are generated. Order not valid !
  private val generatedCpId = mutable.Set.empty[Int]

  // Define the maximum number of passes. After that, the resolver will stop.
  private val MaxPasses = 20

  // Component code already generated or not
  private val nextPassCpId = mutable.Set.empty[Int]

  // Count the number of passes to generate the code
  private var nbrOfPasses = 0

  /**
   * Resolve a graph of components. Unconnected components are ignored.
   * The resolver do nothing if there is less than two connected components. After a maximum of `MaxPasses` iterations,
   * the `Resolver` stops automatically. `None` is returned if there is nothing to resolve or if it fails.
   * @return the list of hardware to resolve, in the right order, or `None` if it fails
   */
  def resolveGraph(): Option[Seq[hw_implemented]] = {

    val connectedNbr = ComponentManager.numberOfConnectedHardware()
    val unconnectedNbr = ComponentManager.numberOfUnconnectedHardware()

    // At least two connected components, or nothing to do...
    if (connectedNbr < 2) {
      info(s"Nothing to resolve: $connectedNbr components ($unconnectedNbr unconnected)")
      return None // Nothing to resolve
    }
    else {
      info(s"Resolver started for $connectedNbr components ($unconnectedNbr unconnected)")
    }

    val cp = mutable.ArrayBuffer.empty[hw_implemented]
    do {
      cp ++= nextPass
    } while (generatedCpId.size != connectedNbr && nbrOfPasses < MaxPasses)

    if (generatedCpId.size == connectedNbr) {
      info(s"Resolver ended successfully after $getNumberOfPasses passes for ${generatedCpId.size} connected " +
        s"components")
      info("Result is: " + cp.mkString(", "))
      return Some(cp)
    }

    error(s"Resolver stopped after $getNumberOfPasses passes")
    None // Infinite loop
  }

  def getNumberOfPasses = nbrOfPasses

  private def nextPass: Seq[hw_implemented] = {

    startPass()

    nbrOfPasses match {
      // First passe. Generate code for all inputs.
      case 0 =>
        val in = ComponentManager.findConnectedInputHardware
        for (c <- in) {
          info(s" > Generate code for: $c")
          val cp = c.asInstanceOf[Component]
          codeGeneratedFor(cp.getId)
        }
        endPass()
        in // HW component to generate

      // From the second passe, the code of all direct successors of the components is generated.
      case _ =>
        val genCp = mutable.Set.empty[hw_implemented]
        val genId = nextPassCpId.clone()
        nextPassCpId.clear()

        for (id <- genId) {
          // Find direct successors for all components of the previous phase
          val successors = ComponentManager.getNode(id).diSuccessors
          for (n <- successors) {
            val cp = n.value.asInstanceOf[Component]
            val gen = isCodeGenerated(cp.getId)
            if (!gen) {
              info(s" > Generate code for: $cp")
              codeGeneratedFor(cp.getId)
              genCp += cp.asInstanceOf[hw_implemented]
            }
            else {
              // Code of this component already generated
              info(s" > Done for: $cp")
            }
          }
        }
        endPass()
        genCp.toSeq // HW component to generate
    }
  }

  private def startPass() = {
    info("Pass [%03d]".format(nbrOfPasses + 1))
  }

  private def endPass() = {
    nbrOfPasses += 1 // Count the number of phase
  }

  private def codeGeneratedFor(cpId: Int) = {
    generatedCpId += cpId // Add ID to the global list
    nextPassCpId += cpId // Code to generate on the next pass
  }

  // Check if the code of the component has been already generated or not
  private def isCodeGenerated(cpId: Int): Boolean = generatedCpId contains cpId
}
