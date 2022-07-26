
import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"                        %% "bootstrap-backend-play-28"  % "5.16.0",
    "uk.gov.hmrc.mongo"                  %% "hmrc-mongo-play-28"         % "0.68.0"
  )

  val test = Seq(
    "uk.gov.hmrc"                        %% "bootstrap-test-play-28"     % "5.16.0"  % Test,
    "org.scalatest"                      %% "scalatest"                  % "3.2.9"   % "test, it",
    "com.typesafe.play"                  %% "play-test"                  % current   % Test,
    "com.vladsch.flexmark"               %  "flexmark-all"               % "0.36.8"  % "test, it",
    "org.scalatestplus.play"             %% "scalatestplus-play"         % "5.0.0"   % "test, it",
    "com.github.tomakehurst"             % "wiremock-jre8"               % "2.29.1"  % IntegrationTest,
    "org.mockito"                        %% "mockito-scala"              % "1.16.37" % Test,
    "org.mockito"                        %% "mockito-scala-scalatest"    % "1.16.37" % Test,
    "com.fasterxml.jackson.module"       %% "jackson-module-scala"       % "2.12.2"  % "test, it",
    "uk.gov.hmrc.mongo"                  %% "hmrc-mongo-test-play-28"    % "0.68.0"  % Test
  )
}
