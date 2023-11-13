ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "lab6"
  )

libraryDependencies += "xyz.devfortress.splot" % "splot-core_2.13" % "0.5.0"