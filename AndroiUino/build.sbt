name := "AndroiUino"

version := "1.0"

scalaVersion := "2.11.1" // Required by the graph librairies

// Graph for Scala
// http://www.scala-graph.org/
libraryDependencies ++= Seq(
  "com.assembla.scala-incubator" %% "graph-core" % "1.9.0",
  "com.assembla.scala-incubator" %% "graph-dot" % "1.9.0"
)

// Grizzled-SLF4J, a Scala-friendly SLF4J Wrapper
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.clapper" %% "grizzled-slf4j" % "1.0.2"
)

// Scala tests
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

// Disable parallel execution of tests because the ComponentManager is a single object used for all tests
parallelExecution in ThisBuild := false

// Scala compiler options
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
