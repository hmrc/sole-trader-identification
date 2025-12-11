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

import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, InternalServerException, StringContextOps}
import uk.gov.hmrc.soletraderidentification.config.AppConfig
import uk.gov.hmrc.soletraderidentification.connectors.CreateTemporaryReferenceHttpParser._
import uk.gov.hmrc.soletraderidentification.models.{Address, FullName}
import play.api.libs.ws.writeableOf_JsValue

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateTemporaryReferenceNumberConnector @Inject()(httpClientV2: HttpClientV2,
                                                        appConfig: AppConfig
                                                       )(implicit ec: ExecutionContext) {

  def createTemporaryReferenceNumber(dateOfBirth: LocalDate,
                                     fullName: FullName,
                                     address: Address
                                    )(implicit hc: HeaderCarrier): Future[String] = {
    val jsonBody: JsObject =
      Json.obj(
        "birthDate" -> dateOfBirth,
        "name" -> Json.obj(
          "forename" -> fullName.firstName,
          "surname" -> fullName.lastName
        ),
        "address" -> Json.toJson(address)
      )

    httpClientV2
      .post(url"${appConfig.createTemporaryReferenceNumberUrl}")
      .withBody(jsonBody)
      .setHeader("Authorization" -> appConfig.integrationFrameworkAuthorizationToken)
      .setHeader("Environment" -> appConfig.integrationFrameworkEnvironment)
      .setHeader("OriginatorId" -> appConfig.integrationFrameworkOriginatorId)
      .setHeader("Content-Type" -> "application/json")
      .execute[String](using CreateTemporaryReferenceHttpReads, ec)
  }
}

object CreateTemporaryReferenceHttpParser {

  object CreateTemporaryReferenceHttpReads extends HttpReads[String] {
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