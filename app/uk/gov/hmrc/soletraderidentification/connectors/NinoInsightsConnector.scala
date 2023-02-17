/*
 * Copyright 2023 HM Revenue & Customs
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
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpClient, HttpReads, HttpResponse, InternalServerException}
import uk.gov.hmrc.soletraderidentification.config.AppConfig
import uk.gov.hmrc.soletraderidentification.connectors.NinoReputationParser.NinoReputationHttpReads

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NinoInsightsConnector @Inject()(http: HttpClient,
                                      appConfig: AppConfig
                                     )(implicit ec: ExecutionContext) {

  def retrieveNinoInsight(nino: String)(implicit hc: HeaderCarrier): Future[JsObject] = {
    val jsonBody = Json.obj(
      "nino" -> nino
    )

    http.POST[JsObject, JsObject](appConfig.getInsightUrl, body = jsonBody)(implicitly[Writes[JsObject]],
      NinoReputationHttpReads,
      hc.copy(authorization = Some(Authorization(appConfig.internalAuthToken))),
      ec
    )
  }

}

object NinoReputationParser {

  object NinoReputationHttpReads extends HttpReads[JsObject] {
    override def read(method: String, url: String, response: HttpResponse): JsObject = {
      response.status match {
        case OK => response.json.validate[JsObject] match {
          case JsSuccess(response, _) => response
          case JsError(errors) => throw new InternalServerException("Nino Insights call failed with error: " + errors)
        }
        case status => throw new InternalServerException("Unexpected status returned from the Nino Insights call: " + status)
      }
    }
  }
}

