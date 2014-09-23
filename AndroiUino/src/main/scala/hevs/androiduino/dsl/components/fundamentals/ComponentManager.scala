package hevs.androiduino.dsl.components.fundamentals

import grizzled.slf4j.Logging
import scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._
import scalax.collection.GraphEdge.DiEdge

object idGenerator {
   private var id = 0

   def getUniqueID() = {
      id += 1
      id
   }
}

object ComponentManager extends Logging {

   // This contains a graph representation of the components
   // that can be skimmed later on
   var gr1: Graph[Component, DiEdge] = Graph()

   // TODO : this can be removed by using directly the gr1 graph
   //var comps: List[Component] = List.empty[Component]

   def addComponent(c: Component): Int = {
      val id = idGenerator.getUniqueID()
      info(s"Creating a new component with id $id of " + c.getClass().getName())
      gr1 += c // Add the component node to the graph
      id
   }

   // TODO beautify this (factorize it)
   def generateInitCode() = {
      var result = ""

      for (c ← gr1.nodes.toList) {
         // In the graph we have nodes. Each node has a content which is a component
         val comp = c.value
         assert(comp.isInstanceOf[Component])
         if (comp.isInstanceOf[hw_implemented]) {
            val hw_c = comp.asInstanceOf[hw_implemented]

            if (hw_c.getInitCode.isDefined)
               result += hw_c.getInitCode.get + "\n"
         }
      }
      result
   }

   def generateBeginMainCode() = {
      var result = ""

      for (c ← gr1.nodes.toList) {
         val comp = c.value
         assert(comp.isInstanceOf[Component])

         if (comp.isInstanceOf[hw_implemented]) {
            val hw_c = comp.asInstanceOf[hw_implemented]

            if (hw_c.getBeginOfMainAfterInit.isDefined)
               result += hw_c.getBeginOfMainAfterInit.get + "\n"
         }
      }
      result
   }

   def generateConstantsCode() = {
      var result = ""

      for (c ← gr1.nodes.toList) {
         val comp = c.value
         assert(comp.isInstanceOf[Component])
      
         if (comp.isInstanceOf[hw_implemented]) {
            val hw_c = comp.asInstanceOf[hw_implemented]

            if (hw_c.getGlobalConstants.isDefined)
               result += hw_c.getGlobalConstants.get + "\n"
         }
      }
      result
   }

   def generateFunctionsCode() = {
      var result = ""

      // Works but not really clearer
      //		val filteredInstances : List[hw_implemented] = comps.filter(_.isInstanceOf[hw_implemented]).map(_.asInstanceOf[hw_implemented])
      //		val functionCode = filteredInstances.map(x => x.getFunctionsDefinitions()).foldLeft("")(_ + _)

      for (c ← gr1.nodes.toList) {
         val comp = c.value
         assert(comp.isInstanceOf[Component])
      
         if (comp.isInstanceOf[hw_implemented]) {
            val hw_c = comp.asInstanceOf[hw_implemented]

            if (hw_c.getFunctionsDefinitions.isDefined)
               result += hw_c.getFunctionsDefinitions.get + "\n"
         }
      }

      result
   }

   def generateLoopingCode() = {
      var result = ""
      // TODO find a guard for making writable on a single line (for refactoring)
      //for (c ← gr1.nodes.toList; if c.value.isInstanceOf[hw_implemented]) { // TODO Does not work, why ?
      for (c ← gr1.nodes.toList) { 
         val comp = c.value
         assert(comp.isInstanceOf[Component])
         
         if(comp.isInstanceOf[hw_implemented]){
         val hw_c = comp.asInstanceOf[hw_implemented]

         if (hw_c.getLoopableCode.isDefined)
            result += "\t\t" + hw_c.getLoopableCode.get + "\n"
            
      	}
      
      }
      result
   }
}