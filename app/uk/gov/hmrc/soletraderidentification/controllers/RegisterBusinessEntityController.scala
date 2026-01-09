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

package uk.gov.hmrc.soletraderidentification.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.soletraderidentification.connectors.RegisterWithMultipleIdentifiersHttpParser._
import uk.gov.hmrc.soletraderidentification.services.RegisterWithMultipleIdentifiersService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RegisterBusinessEntityController @Inject()(cc: ControllerComponents,
                                                 registerWithMultipleIdentifiersService: RegisterWithMultipleIdentifiersService,
                                                 val authConnector: AuthConnector
                                                )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthorisedFunctions {

  def registerWithNino(): Action[(String, Option[String], String)] = Action.async(parse.json[(String, Option[String], String)](using json => for {
    nino <- (json \ "soleTrader" \ "nino").validate[String]
    sautr <- (json \ "soleTrader" \ "sautr").validateOpt[String]
    regime <- (json \ "soleTrader" \ "regime").validate[String]
  } yield (nino, sautr, regime))) {
    implicit request =>
      authorised() {
        val (nino, optSautr, regime) = request.body
        registerWithMultipleIdentifiersService.registerWithNino(nino, optSautr, regime).map {
          case RegisterWithMultipleIdentifiersSuccess(safeId) =>
            Ok(Json.obj(
              "registration" -> Json.obj(
                "registrationStatus" -> "REGISTERED",
                "registeredBusinessPartnerId" -> safeId)))
          case RegisterWithMultipleIdentifiersFailure(status, body) =>
            Ok(Json.obj("registration" -> Json.obj(
              "registrationStatus" -> "REGISTRATION_FAILED",
              "failures" -> body)
            ))
        }
      }
  }

  def registerWithTrn(): Action[(String, String, String)] = Action.async(parse.json[(String, String, String)](using json => for {
    nino <- (json \ "trn").validate[String]
    sautr <- (json \ "sautr").validate[String]
    regime <- (json \ "regime").validate[String]
  } yield (nino, sautr, regime))) {
    implicit request =>
      authorised() {
        val (trn, sautr, regime) = request.body
        registerWithMultipleIdentifiersService.registerWithTrn(trn, sautr, regime).map {
          case RegisterWithMultipleIdentifiersSuccess(safeId) =>
            Ok(Json.obj(
              "registration" -> Json.obj(
                "registrationStatus" -> "REGISTERED",
                "registeredBusinessPartnerId" -> safeId)))
          case RegisterWithMultipleIdentifiersFailure(_, body) =>
            Ok(Json.obj("registration" -> Json.obj(
              "registrationStatus" -> "REGISTRATION_FAILED",
              "failures" -> body)
            ))
        }
      }
  }
}