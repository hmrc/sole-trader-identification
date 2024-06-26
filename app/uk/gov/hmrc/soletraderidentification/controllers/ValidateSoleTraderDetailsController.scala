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
import uk.gov.hmrc.soletraderidentification.models.{DetailsMatched, DetailsMismatched, DetailsNotFound, SoleTraderDetailsModel}
import uk.gov.hmrc.soletraderidentification.services.ValidateSoleTraderDetailsService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ValidateSoleTraderDetailsController @Inject()(cc: ControllerComponents,
                                                    validateIncorporatedEntityDetailsService: ValidateSoleTraderDetailsService,
                                                    val authConnector: AuthConnector
                                                   )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthorisedFunctions {

  def validateDetails(): Action[SoleTraderDetailsModel] = Action.async(parse.json[SoleTraderDetailsModel]) {
    implicit request =>
      authorised() {
        validateIncorporatedEntityDetailsService.validateDetails(request.body.nino, request.body.sautr).map {
          case DetailsMatched =>
            Ok(Json.obj("matched" -> true))
          case DetailsMismatched =>
            Ok(Json.obj("matched" -> false))
          case DetailsNotFound =>
            BadRequest(Json.obj(
              "code" -> "NOT_FOUND",
              "reason" -> "The back end has indicated that SA UTR cannot be returned")
            )
        }
      }
  }

}
