name := "especial"
version := "1.0"

scalaVersion := "2.11.4"

// Graph for Scala - http://www.scala-graph.org/
libraryDependencies ++= Seq(
  "com.assembla.scala-incubator" %% "graph-core" % "1.9.1",
  "com.assembla.scala-incubator" %% "graph-dot" % "1.10.0",
  "com.assembla.scala-incubator" %% "graph-constrained" % "1.9.0"
)

// Grizzled-SLF4J, a Scala-friendly SLF4J Wrapper
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.clapper" %% "grizzled-slf4j" % "1.0.2"
)

// Lift-json - http://liftweb.net/download
libraryDependencies += "net.liftweb" %% "lift-json" % "2.6-M4"

// Scala tests - http://www.scalatest.org/
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

// Scala compiler options
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")


// Disable parallel execution of tests because the ComponentManager is a single object used for all tests.
parallelExecution in ThisBuild := false

// Remove some tests which must be ran manually, one after one.
testOptions in Test := Seq(Tests.Filter(s => !(s.contains("apps.") || s.contains("generator.") ||
  s.contains("simulation.") || s.contains("doc."))))


// Generate ScalaDoc diagrams using dot
scalacOptions in(Compile, doc) ++= Seq("-diagrams")


// Custom clean task
// Delete all generated files from the output directory
clean ~= {x => println("Remove generated output files...")}
cleanFiles <++= baseDirectory { base => (base / "output/" * "*").get }
