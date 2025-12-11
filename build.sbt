import uk.gov.hmrc.DefaultBuildSettings

val appName = "sole-trader-identification"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.7.4"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // Use Scala options to reduce compiler warnings
    scalacOptions += "-Wconf:src=routes/.*&msg=unused import:silent",
    scalacOptions += "-Wconf:src=routes/.*&msg=unused private member:silent",
    scalacOptions += "-Wconf:src=routes/.*&msg=unused pattern variable:silent",
  )
  .settings(CodeCoverageSettings.settings *)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.it)