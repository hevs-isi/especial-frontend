package hevs.androiduino.dsl.components

import hevs.androiduino.dsl.components.fundamentals.hw_implemented
import hevs.androiduino.dsl.components.fundamentals.Component
import hevs.androiduino.dsl.components.fundamentals.C_Types
import hevs.androiduino.dsl.components.fundamentals.OutputPort

private object UniqueID {
   private var id = 0
   def getUniqueID() = { id = id + 1; id }
}

case class Constant(t: C_Types) extends Component("a constant generator") with hw_implemented {
   val valName = "_autoCst" + UniqueID.getUniqueID

   val out = new OutputPort(t, this) {
      def getValue(): String = {
         return valName
      }
   }

   def getOutputs() = out :: Nil
   def getInputs() = Nil

   override def getGlobalConstants(): Option[String] = {
      val value = t.v
      Some(s"const $t $valName = $value;")
   }

   override def getBeginOfMainAfterInit() = {
      var result = ""

      result += "// Propagating constants\n"

      for (wire ← out.wires) {
         result += wire.b.updateValue(s"$valName") + ";\n"
      }

      Some(result)
   }

   // We need to define equality for our objects
   override def equals(other: Any) = {
      other match {
         case that: Constant ⇒ that.valName == this.valName
         case _ ⇒ false
      }
   }

   // Hashcode also required for working with graphs
   // The meaning of ## is cryptic here but related to hashcode equality
   // for autoboxed type in Java 
   override def hashCode() = valName.##
   
   override def toString() = "POUET !!!"
}