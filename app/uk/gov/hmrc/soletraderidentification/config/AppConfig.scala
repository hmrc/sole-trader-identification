/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.soletraderidentification.config

import org.apache.commons.io.IOUtils
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.soletraderidentification.featureswitch.core.config.{CreateTrnStub, DesStub, FeatureSwitching, StubGetSaReference}

import javax.inject.{Inject, Singleton}
import scala.io.Source

@Singleton
class AppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig, environment: Environment) extends FeatureSwitching {

  val authBaseUrl: String = servicesConfig.baseUrl("auth")

  val auditingEnabled: Boolean = config.get[Boolean]("auditing.enabled")
  val graphiteHost: String = config.get[String]("microservice.metrics.graphite.host")

  def getSaReferenceUrl(nino: String): String = {
    val baseUrl = if (isEnabled(StubGetSaReference)) desStubBaseUrl else desBaseUrl
    s"$baseUrl/corporation-tax/identifiers/nino/$nino"
  }

  def getRegisterWithMultipleIdentifiersUrl(regime: String): String = {
    val baseUrl = if (isEnabled(DesStub)) desStubBaseUrl else desBaseUrl
    s"$baseUrl/cross-regime/register/GRS?grsRegime=$regime"
  }

  def createTemporaryReferenceNumberUrl: String = {
    val baseUrl = if (isEnabled(CreateTrnStub)) integrationFrameworkStubBaseUrl else integrationFrameworkBaseUrl
    s"$baseUrl/individuals/trn"
  }

  lazy val desBaseUrl: String = servicesConfig.getString("microservice.services.des.url")

  lazy val desStubBaseUrl: String = servicesConfig.getString("microservice.services.des.stub-url")

  lazy val integrationFrameworkBaseUrl: String =
    servicesConfig.getString("microservice.services.integration-framework.url")

  lazy val integrationFrameworkStubBaseUrl: String =
    servicesConfig.getString("microservice.services.integration-framework.stub-url")

  lazy val desAuthorisationToken: String =
    s"Bearer ${servicesConfig.getString("microservice.services.des.authorisation-token")}"

  lazy val desEnvironment: String =
    servicesConfig.getString("microservice.services.des.environment")

  lazy val integrationFrameworkEnvironment: String =
    servicesConfig.getString("microservice.services.integration-framework.environment")

  lazy val integrationFrameworkOriginatorId: String =
    servicesConfig.getString("microservice.services.integration-framework.originator-id")

  lazy val integrationFrameworkAuthorizationToken: String =
    s"Bearer ${servicesConfig.getString("microservice.services.integration-framework.authorization-token")}"

  val timeToLiveSeconds: Int = servicesConfig.getInt("mongodb.timeToLiveSeconds")

  def readFraudulentNinosFile: Set[String] = {
    val configurationKey = "sole-trader-identification.fraudulent-ninos-file-name"
    environment.resourceAsStream(config.get[String](configurationKey)) match {
      case Some(underneathInputStream) =>
        try {
          Source.fromInputStream(underneathInputStream).getLines().toSet
        } finally {
          IOUtils.close(underneathInputStream)
        }
      case None =>
        throw new IllegalArgumentException(s"Not file found using configuration key $configurationKey")
    }
  }

}
