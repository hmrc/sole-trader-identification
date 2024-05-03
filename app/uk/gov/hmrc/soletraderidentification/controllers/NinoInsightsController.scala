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

import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.soletraderidentification.services.NinoInsightsService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class NinoInsightsController @Inject()(cc: ControllerComponents,
                                       val authConnector: AuthConnector,
                                       ninoInsightsService: NinoInsightsService
                                      )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthorisedFunctions {

  def retrieveNinoInsight(): Action[String] = Action.async(parse.json[String](json => for {
    nino <- (json \ "nino").validate[String]
  } yield nino)) {
    implicit request =>
      authorised() {
        val nino = request.body
        ninoInsightsService.retrieveNinoInsight(nino).map {
          insights => Ok(insights)
        }
      }
  }

}
