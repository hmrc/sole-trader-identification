/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.soletraderidentification.connectors

import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.soletraderidentification.config.AppConfig
import uk.gov.hmrc.soletraderidentification.httpparsers.GetSaReferenceHttpParser.GetSaReferenceHttpReads

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetSaReferenceConnector @Inject()(httpClientV2: HttpClientV2,
                                        appConfig: AppConfig
                                       )(implicit ec: ExecutionContext) {

  def getSaReference(nino: String)(implicit hc: HeaderCarrier): Future[Option[String]] = {
    httpClientV2
      .get(url"${appConfig.getSaReferenceUrl(nino)}")
      .setHeader("Authorization" -> appConfig.desAuthorisationToken)
      .setHeader("Environment" -> appConfig.desEnvironment)
      .execute[Option[String]]
  }
}
