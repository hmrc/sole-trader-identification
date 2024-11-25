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

import play.api.http.Status.OK
import play.api.libs.json._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpReads, HttpResponse, InternalServerException, StringContextOps}
import uk.gov.hmrc.soletraderidentification.config.AppConfig
import uk.gov.hmrc.soletraderidentification.connectors.NinoInsightsConnector.NinoReputationHttpReads

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NinoInsightsConnector @Inject()(httpClientV2: HttpClientV2, appConfig: AppConfig)(implicit ec: ExecutionContext) {

  def retrieveNinoInsight(nino: String)(implicit hc: HeaderCarrier): Future[JsObject] = {
    val jsonBody = Json.obj(
      "nino" -> nino
    )

    val modifiedHc = hc.copy(authorization = Some(Authorization(appConfig.internalAuthToken)))

    httpClientV2
      .post(url"${appConfig.getInsightUrl}")(modifiedHc)
      .withBody(jsonBody)
      .execute[JsObject](NinoReputationHttpReads, ec)
  }

}

object NinoInsightsConnector {

  object NinoReputationHttpReads extends HttpReads[JsObject] {
    override def read(method: String, url: String, response: HttpResponse): JsObject =
      response.status match {
        case OK =>
          response.json.validate[JsObject] match {
            case JsSuccess(response, _) => response
            case JsError(errors)        => throw new InternalServerException("Nino Insights call failed with error: " + errors)
          }
        case status => throw new InternalServerException("Unexpected status returned from the Nino Insights call: " + status)
      }
  }
}
