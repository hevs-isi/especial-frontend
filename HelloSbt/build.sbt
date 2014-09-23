// Sample SBT project
// Date: 23.09.2014
// Author: christopher.metrailler@epfl.ch

name := "HelloSbt"

version := "1.0"

scalaVersion := "2.11.2"

// String sample task
val sampleStringTask = taskKey[String]("A sample string task.")

sampleStringTask := {
	println("My sampleStringTask runs:")
	System.getProperty("user.home")
}
