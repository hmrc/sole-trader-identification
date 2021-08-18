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
import uk.gov.hmrc.soletraderidentification.models.{Address, FullName}
import uk.gov.hmrc.soletraderidentification.services.GetTemporaryReferenceService

import java.time.LocalDate
import scala.concurrent.ExecutionContext

@Singleton
class GetTemporaryReferenceController @Inject()(cc: ControllerComponents,
                                                getTemporaryReferenceService: GetTemporaryReferenceService,
                                                val authConnector: AuthConnector
                                                   )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthorisedFunctions {

  def getTrn(): Action[JsValue] = Action.async(parse.json){
    implicit request =>
      authorised() {
        val dateOfBirth = (request.body \"dateOfBirth").as[LocalDate]
        val fullName = (request.body \"fullName").as[FullName]
        val address = (request.body \ "address").as[Address]
      getTemporaryReferenceService.getTemporaryReference(dateOfBirth, fullName, address).map{
        trn => Created (Json.obj("temporaryReferenceNumber" -> trn))
      }}
      }

}
