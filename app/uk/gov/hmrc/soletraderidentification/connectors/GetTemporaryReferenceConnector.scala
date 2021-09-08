/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.http.Status._
import play.api.libs.json.{JsError, JsObject, JsSuccess, Json, Writes}

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse, InternalServerException}
import uk.gov.hmrc.soletraderidentification.config.AppConfig
import uk.gov.hmrc.soletraderidentification.connectors.Parser.TrnHttpReads
import uk.gov.hmrc.soletraderidentification.models.{Address, FullName}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetTemporaryReferenceConnector @Inject()(http: HttpClient,
                                               appConfig: AppConfig
                                              )(implicit ec: ExecutionContext) {

  def getTrn(dateOfBirth: LocalDate,
             fullName: FullName,
             address: Address
            )(implicit hc: HeaderCarrier): Future[String] = {
    val extraHeaders = Seq(
      "Authorization" -> appConfig.integrationFrameworkAuthorizationHeader,
      "Environment" -> appConfig.integrationFrameworkEnvironmentHeader,
      "Content-Type" -> "application/json"
    )
    val jsonBody: JsObject =
      Json.obj(
        "birthDate" -> dateOfBirth,
        "name" -> Json.obj(
          "forename" -> fullName.firstName,
          "surname" -> fullName.lastName
        ),
        "address" -> Json.toJson(address)
      )

    http.POST[JsObject, String](
      url = appConfig.getTemporaryReferenceNumberUrl,
      body = jsonBody,
      headers = extraHeaders
    )(implicitly[Writes[JsObject]],
      TrnHttpReads,
      hc,
      ec
    )
  }

}

object Parser {
  implicit object TrnHttpReads extends HttpReads[String] {

    override def read(method: String, url: String, response: HttpResponse): String = {
      response.status match {
        case CREATED =>
          (response.json \ "temporaryReferenceNumber").validate[String] match {
            case JsSuccess(trn, _) =>
              trn
            case JsError(errors) =>
              throw new InternalServerException(s"Get TRN returned malformed JSON with the following errors: $errors")
          }
        case _ =>
          throw new InternalServerException(
            s"Invalid response on TRN API with status: ${response.status}, body: ${response.body} and headers: ${response.headers}"
          )
      }
    }
  }
}