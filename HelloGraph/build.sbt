
name := "HelloGraph"

version := "1.0"

scalaVersion := "2.11.1" // For graph-core

libraryDependencies += "com.assembla.scala-incubator" %% "graph-core" % "1.9.0"

libraryDependencies += "com.assembla.scala-incubator" %% "graph-dot" % "1.9.0"

// Scala compiler options
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")