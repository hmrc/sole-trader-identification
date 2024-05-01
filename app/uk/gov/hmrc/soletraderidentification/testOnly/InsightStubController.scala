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

package uk.gov.hmrc.soletraderidentification.testOnly

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.soletraderidentification.config.AppConfig

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class InsightStubController @Inject()(controllerComponents: ControllerComponents, appConfig: AppConfig)
  extends BackendController(controllerComponents) {

  def retrieveInsight: Action[JsValue] = Action.async(parse.json) {
    implicit request =>

      val correlationId = UUID.randomUUID().toString

      (request.body \ "nino").as[String] match {
        case "AA222222B" => Future.successful(Ok(Json.obj(
          "ninoInsightsCorrelationId" -> correlationId,
          appConfig.insightResult -> 100,
          "reason" -> appConfig.insightMessages.head
        )))
        case _ => Future.successful(Ok(Json.obj(
          "ninoInsightsCorrelationId" -> correlationId,
          appConfig.insightResult -> 0,
          "reason" -> appConfig.insightMessages.last
        )))
      }
  }

}
