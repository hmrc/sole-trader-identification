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

package uk.gov.hmrc.soletraderidentification.httpparsers

import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse, InternalServerException}

object GetSaReferenceHttpParser {

  val sautrKey = "SAUTR"

  implicit object GetSaReferenceHttpReads extends HttpReads[Option[String]] {
    override def read(method: String, url: String, response: HttpResponse): Option[String] = {
      response.status match {
        case OK =>
          (response.json \ sautrKey).validate[String] match {
            case JsSuccess(ctutr, _) =>
              Some(ctutr)
            case JsError(errors) =>
              throw new InternalServerException(s"Get SA Reference returned malformed JSON with the following errors: $errors")
          }
        case NOT_FOUND =>
          None
        case status =>
          throw new InternalServerException(s"Get SA Reference failed with status: $status, body: ${response.body} and headers: ${response.headers}")
      }
    }
  }

}