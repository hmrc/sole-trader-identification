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
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.soletraderidentification.config.AppConfig
import uk.gov.hmrc.soletraderidentification.connectors.RegisterWithMultipleIdentifiersHttpParser._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegisterWithMultipleIdentifiersConnector @Inject()(httpClientV2: HttpClientV2,
                                                         appConfig: AppConfig
                                                        )(implicit ec: ExecutionContext) {
  private lazy val extraHeaders = Seq(
    "Authorization" -> appConfig.desAuthorisationToken,
    "Environment" -> appConfig.desEnvironment,
    "Content-Type" -> "application/json"
  )

  implicit val httpReads: HttpReads[RegisterWithMultipleIdentifiersResult] = RegisterWithMultipleIdentifiersHttpReads

  private def postToRegisterEndpoint(jsonBody: JsObject, regime: String)(implicit hc: HeaderCarrier) = {
    httpClientV2
      .post(url"${appConfig.getRegisterWithMultipleIdentifiersUrl(regime)}")
      .withBody(jsonBody)
      .setHeader(extraHeaders:_*)
      .execute[RegisterWithMultipleIdentifiersResult]
  }

  def registerWithNino(nino: String,
                       optSautr: Option[String],
                       regime: String
                      )(implicit hc: HeaderCarrier): Future[RegisterWithMultipleIdentifiersResult] = {

    val soleTraderIdentifiers = optSautr match {
      case Some(sautr) => Json.obj(
        "nino" -> nino,
        "sautr" -> sautr
      )
      case None => Json.obj(
        "nino" -> nino
      )
    }

    val jsonBody: JsObject = Json.obj(
      "soleTrader" -> soleTraderIdentifiers
    )

    postToRegisterEndpoint(jsonBody, regime)
  }

  def registerWithTrn(trn: String,
                      sautr: String,
                      regime: String
                     )(implicit hc: HeaderCarrier): Future[RegisterWithMultipleIdentifiersResult] = {

    val jsonBody: JsObject =
      Json.obj(
        "soleTrader" ->
          Json.obj(
            "tempNI" -> trn,
            "sautr" -> sautr
          )
      )

    postToRegisterEndpoint(jsonBody, regime)
  }

}

object RegisterWithMultipleIdentifiersHttpParser {

  val IdentificationKey = "identification"
  val IdentificationTypeKey = "idType"
  val IdentificationValueKey = "idValue"
  val SafeIdKey = "SAFEID"

  implicit object RegisterWithMultipleIdentifiersHttpReads extends HttpReads[RegisterWithMultipleIdentifiersResult] {

    override def read(method: String, url: String, response: HttpResponse): RegisterWithMultipleIdentifiersResult = {
      response.status match {
        case OK =>
          (for {
            idType <- (response.json \ IdentificationKey \ 0 \ IdentificationTypeKey).validate[String]
            if idType == SafeIdKey
            safeId <- (response.json \ IdentificationKey \ 0 \ IdentificationValueKey).validate[String]
          } yield safeId) match {
            case JsSuccess(safeId, _) => RegisterWithMultipleIdentifiersSuccess(safeId)
            case _: JsError => throw new InternalServerException(s"Invalid JSON returned on Register API: ${response.body}")
          }
        case _ => if (response.json.as[JsObject].keys.contains("failures")) {
          (response.json \ "failures").validate[Array[Failures]] match {
            case JsSuccess(failures, _) => RegisterWithMultipleIdentifiersFailure(response.status, failures)
            case _: JsError => throw new InternalServerException(s"Invalid JSON returned on Register API: ${response.body}")
          }
        } else {
          response.json.validate[Failures] match {
            case JsSuccess(failure, _) => RegisterWithMultipleIdentifiersFailure(response.status, Array(failure))
            case _: JsError => throw new InternalServerException(s"Invalid JSON returned on Register API: ${response.body}")
          }
        }
      }
    }
  }

  sealed trait RegisterWithMultipleIdentifiersResult

  case class RegisterWithMultipleIdentifiersSuccess(safeId: String) extends RegisterWithMultipleIdentifiersResult

  case class RegisterWithMultipleIdentifiersFailure(status: Int, body: Array[Failures]) extends RegisterWithMultipleIdentifiersResult

  case class Failures(code: String, reason: String)

  implicit val format: OFormat[Failures] = Json.format[Failures]

}
