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

package uk.gov.hmrc.soletraderidentification.services

import play.api.libs.json.{JsObject, JsValue}
import uk.gov.hmrc.soletraderidentification.repositories.JourneyDataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyDataService @Inject()(journeyDataRepository: JourneyDataRepository,
                                   journeyIdGenerationService: JourneyIdGenerationService
                                  )(implicit ec: ExecutionContext) {


  def createJourney(authInternalId: String): Future[String] = {
    val journeyId = journeyIdGenerationService.generateJourneyId()
    journeyDataRepository.createJourney(journeyId, authInternalId)
  }

  def getJourneyData(journeyId: String, authInternalId: String): Future[Option[JsObject]] = {
    journeyDataRepository.getJourneyData(journeyId, authInternalId)
  }

  def getJourneyDataByKey(journeyId: String, dataKey: String, authInternalId: String): Future[Option[JsValue]] = {
    journeyDataRepository.getJourneyData(journeyId, authInternalId).map {
      case Some(json) => (json \ dataKey).asOpt[JsValue]
      case None => None
    }
  }

  def updateJourneyData(journeyId: String, authInternalId: String, dataKey: String, data: JsValue): Future[Boolean] =
    journeyDataRepository.updateJourneyData(journeyId, authInternalId, dataKey, data)

  def removeJourneyDataField(journeyId: String, authInternalId: String, dataKey: String): Future[Boolean] = {
    journeyDataRepository.removeJourneyDataField(journeyId, authInternalId, dataKey)
  }

  def removeJourneyData(journeyId: String, authInternalId: String): Future[Boolean] = {
    journeyDataRepository.removeJourneyData(journeyId, authInternalId)
  }

}
