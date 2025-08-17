ThisBuild / licenses           := Seq("ISC" -> url("https://opensource.org/licenses/ISC"))
ThisBuild / versionScheme      := Some("semver-spec")
ThisBuild / evictionErrorLevel := Level.Warn

publish / skip := true

lazy val apion_template = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
//  .enablePlugins(ScalablyTypedConverterPlugin)
  .settings(
    name         := "apion_template",
    version      := "0.0.1",
    scalaVersion := "3.7.2",
    organization := "io.github.edadma",
//    libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % "2.6.0",
    libraryDependencies ++= Seq(
      "org.scalatest"    %%% "scalatest"                   % "3.2.19" % "test",
      "org.scala-js"     %%% "scala-js-macrotask-executor" % "1.1.1",
      "io.github.edadma" %%% "apion"                       % "0.0.8",
    ),
//    libraryDependencies += "com.lihaoyi" %%% "pprint" % "0.9.0" % "test",
    jsEnv                                  := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
    Test / scalaJSUseMainModuleInitializer := true,
    Test / scalaJSUseTestModuleInitializer := false,
//    Test / scalaJSUseMainModuleInitializer := false,
//    Test / scalaJSUseTestModuleInitializer := true,
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    publishMavenStyle      := true,
    Test / publishArtifact := false,
    licenses += "ISC"      -> url("https://opensource.org/licenses/ISC"),
  )
