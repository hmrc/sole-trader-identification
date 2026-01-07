
import play.core.PlayVersion.current
import sbt.*

object AppDependencies {

  val bootStrapVersion: String = "10.5.0"
  val mongoVersion: String = "2.11.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-30" % bootStrapVersion,
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"        % mongoVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-test-play-30"    % bootStrapVersion,
    "org.scalatest"                %% "scalatest"                 % "3.2.19",
    "org.playframework"            %% "play-test"                 % current,
    "com.vladsch.flexmark"         %  "flexmark-all"              % "0.64.8",
    "org.mockito"                   % "mockito-core"              % "5.21.0",
    "org.scalatestplus.play"       %% "scalatestplus-play"        % "7.0.2",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.20.1",
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-test-play-30"   % mongoVersion
  ).map(_ % Test)

  val it: Seq[ModuleID] = Seq(
    "org.wiremock"                 % "wiremock"                   % "3.13.2"           % Test
  )
}
