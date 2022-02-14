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

package uk.gov.hmrc.soletraderidentification.testOnly

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class RegisterWithMultipleIdentifiersStubController @Inject()(controllerComponents: ControllerComponents) extends BackendController(controllerComponents) {

  val registerWithMultipleIdentifiers: Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      val nino: String = (request.body \\ "nino").map(_.as[String]).head

      val stubbedSafeId = nino match {
        case "ZT790941B" => "XH0000100382440" // PPT Testing
        case "ST497186D" => "XY0000100382439" // PPT Testing
        case _ => "X00000123456789"
      }

      Future.successful(Ok(Json.obj(
        "identification" -> Json.arr(
          Json.obj(
            "idType" -> "SAFEID",
            "idValue" -> stubbedSafeId
          )
        ))))
  }
}
