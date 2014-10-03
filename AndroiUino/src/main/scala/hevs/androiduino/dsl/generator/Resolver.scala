package hevs.androiduino.dsl.generator

import grizzled.slf4j.Logging
import hevs.androiduino.dsl.components.ComponentManager
import hevs.androiduino.dsl.components.fundamentals.{Component, hw_implemented}

import scala.collection.mutable

// Unconnected components are ignored !!
class Resolver extends Logging {

  // All id of components that are generated
  private val generatedCpId = mutable.Set.empty[Int]

  // Number of connected components in the graph
  private def nbrOfConnectedCp = ComponentManager.numberOfConnectedHardware()

  // Define the maximum number of passes. After that, the resolver will stop.
  private val MaxPasses = 20
  // Component code already generated or not
  private val nextPassCpId = mutable.Set.empty[Int]
  // Current number of passes to generate the code
  private var nbrOfPasses = 0

  def getNumberOfPasses = nbrOfPasses
  def getNumberOfCodes = generatedCpId.size

  def resolveGraph(): Boolean = {

    val unconnected = ComponentManager.numberOfUnconnectedHardware()

    // At least two connected components, or nothing to do...
    if (nbrOfConnectedCp < 2) {
      info(s"Nothing to resolve: $nbrOfConnectedCp components ($unconnected unconnected)")
      return false
    }

    info(s"Resolver started for $nbrOfConnectedCp components ($unconnected unconnected)")

    var cp = Seq.empty[hw_implemented]
    do {
      cp = nextPass
    } while (generatedCpId.size != nbrOfConnectedCp && nbrOfPasses < MaxPasses)

    if (generatedCpId.size == nbrOfConnectedCp) {
      info(s"Resolver ended successfully after $getNumberOfPasses passes for $getNumberOfCodes components")
      return true
    }

    error(s"Resolver stopped after $getNumberOfPasses passes")
    false
  }

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
        in  // HW component to generate

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
