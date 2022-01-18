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

package uk.gov.hmrc.soletraderidentification.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.soletraderidentification.models.{Address, FullName}
import uk.gov.hmrc.soletraderidentification.services.CreateTemporaryReferenceNumberService

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CreateTemporaryReferenceNumberController @Inject()(cc: ControllerComponents,
                                                         createTemporaryReferenceNumberService: CreateTemporaryReferenceNumberService,
                                                         val authConnector: AuthConnector
                                                        )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthorisedFunctions {

  def createTemporaryReferenceNumber(): Action[(LocalDate, FullName, Address)] = Action.async(parse.json[(LocalDate, FullName, Address)](json => for {
    dateOfBirth <- (json \ "dateOfBirth").validate[LocalDate]
    fullName <- (json \ "fullName").validate[FullName]
    address <- (json \ "address").validate[Address]
  } yield (dateOfBirth, fullName, address))) {
    implicit request =>
      authorised() {
        val (dateOfBirth, fullName, address) = request.body
        createTemporaryReferenceNumberService.createTemporaryReferenceNumber(dateOfBirth, fullName, address).map {
          trn => Created(Json.obj("temporaryReferenceNumber" -> trn))
        }
      }
  }

}
