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

package uk.gov.hmrc.soletraderidentification.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.soletraderidentification.connectors.RegisterWithMultipleIdentifiersHttpParser.{RegisterWithMultipleIdentifiersFailure, RegisterWithMultipleIdentifiersSuccess}
import uk.gov.hmrc.soletraderidentification.services.RegisterWithMultipleIdentifiersService

import scala.concurrent.ExecutionContext

@Singleton
class RegisterBusinessEntityController @Inject()(cc: ControllerComponents,
                                                 registerWithMultipleIdentifiersService: RegisterWithMultipleIdentifiersService,
                                                 val authConnector: AuthConnector
                                                )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthorisedFunctions {

  def register(): Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      authorised() {
        val nino = (request.body \ "soleTrader" \ "nino").as[String]
        val sautr = (request.body \ "soleTrader" \ "sautr").as[String]
        registerWithMultipleIdentifiersService.register(nino, sautr).map {
          case RegisterWithMultipleIdentifiersSuccess(safeId) =>
            Ok(Json.obj(
              "registration" -> Json.obj(
                "registrationStatus" -> "REGISTERED",
                "registeredBusinessPartnerId" -> safeId)))
          case RegisterWithMultipleIdentifiersFailure(status, body) =>
            Ok(Json.obj(
              "registration" -> Json.obj(
                "registrationStatus" -> "REGISTRATION_FAILED")))

        }

      }

  }
}